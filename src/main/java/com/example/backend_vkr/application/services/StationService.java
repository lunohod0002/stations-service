package com.example.backend_vkr.application.services;
import com.example.backend_vkr.domain.repositories.*;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_vkr.application.dto.*;
import com.example.backend_vkr.domain.*;
import com.example.backend_vkr.domain.enums.MediaType;

import com.example.backend_vkr.domain.exceptions.ResourceNotFoundException;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class StationService {
    private final StationRepository stationRepository;
    private final MediaRepository mediaRepository;
    private final StationAttractionsRepository stationAttractionsRepository;
    private final CellTowerRepository cellTowerRepository;
    private final AttractionRepository attractionRepository;

    @Autowired
    public StationService(StationRepository stationRepository,
                          MediaRepository mediaRepository,
                          StationAttractionsRepository stationAttractionsRepository,
                          CellTowerRepository cellTowerRepository,
                          AttractionRepository attractionRepository) {
        this.stationRepository = stationRepository;
        this.mediaRepository = mediaRepository;
        this.stationAttractionsRepository = stationAttractionsRepository;
        this.cellTowerRepository = cellTowerRepository;
        this.attractionRepository = attractionRepository;
    }
    @Transactional
    public StationResponse updateStation(Long id, UpdateStationRequest request) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Станция не найдена:", String.valueOf(id)));

        // Уникальность (name, branch): свои же значения разрешаем,
        // но не даём занять пару, принадлежащую другой станции.
        stationRepository.findByNameAndBranch(request.name(), request.branch())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ResourceNotFoundException(
                            "Станция уже существует: ", request.name() + " (" + request.branch() + ")");
                });

        // 1. Простые поля
        station.setName(request.name());
        station.setBranch(request.branch());
        station.setDescription(request.description());
        if (request.address() != null) {
            station.setAddress(request.address());
        }

        // 2. Медиа — полная замена
        updateStationMedias(station, request.media());

        // 3. Вышки CellTower — полная замена
        updateCellTowers(station, request.cellTowers());

        // 4. Достопримечательности — диффинг по присланному списку
        syncAttractionLinks(station, request.attractions());

        return getStationByNameAndBranch(station.getName(), station.getBranch());
    }

    private void syncAttractionLinks(Station station, List<StationAttractionLinkRequest> incoming) {
        // 1. Нормализуем входной список: attractionId -> distance.
        //    Используем LinkedHashMap, чтобы при дублях побеждал последний (last-wins),
        //    а порядок вставки оставался предсказуемым.
        Map<Long, Integer> incomingByAttractionId = new LinkedHashMap<>();
        if (incoming != null) {
            for (StationAttractionLinkRequest req : incoming) {
                incomingByAttractionId.put(req.attractionId(), req.distance());
            }
        }

        // 2. Текущие привязки станции
        List<StationAttractions> existing =
                stationAttractionsRepository.findByStationId(station.getId());
        Map<Long, StationAttractions> existingByAttractionId = existing.stream()
                .collect(Collectors.toMap(sa -> sa.getAttraction().getId(), sa -> sa));

        // 3. УДАЛЯЕМ всё, чего нет в новом списке
        List<StationAttractions> toDelete = existing.stream()
                .filter(sa -> !incomingByAttractionId.containsKey(sa.getAttraction().getId()))
                .toList();
        if (!toDelete.isEmpty()) {
            stationAttractionsRepository.deleteAll(toDelete);
            stationAttractionsRepository.flush(); // важно: до возможной повторной вставки
        }

        if (incomingByAttractionId.isEmpty()) {
            return;
        }

        // 4. Заранее подгружаем Attraction-ы для будущих НОВЫХ связей (батчем, без N+1)
        Set<Long> idsToCreate = incomingByAttractionId.keySet().stream()
                .filter(attractionId -> !existingByAttractionId.containsKey(attractionId))
                .collect(Collectors.toSet());

        Map<Long, Attraction> newAttractionsById = Map.of();
        if (!idsToCreate.isEmpty()) {
            List<Attraction> found = attractionRepository.findAllById(idsToCreate);
            if (found.size() != idsToCreate.size()) {
                Set<Long> foundIds = found.stream().map(Attraction::getId).collect(Collectors.toSet());
                Set<Long> missing = new HashSet<>(idsToCreate);
                missing.removeAll(foundIds);
                throw new ResourceNotFoundException(
                        "Достопримечательности не найдены:", missing.toString());
            }
            newAttractionsById = found.stream()
                    .collect(Collectors.toMap(Attraction::getId, a -> a));
        }

        // 5. UPDATE (если расстояние изменилось) / CREATE (если новая связь)
        List<StationAttractions> toSave = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : incomingByAttractionId.entrySet()) {
            Long attractionId = entry.getKey();
            int distance = entry.getValue();

            StationAttractions existingLink = existingByAttractionId.get(attractionId);
            if (existingLink != null) {
                if (!Objects.equals(existingLink.getDistance(), distance)) {
                    existingLink.setDistance(distance); // dirty checking сам сделает UPDATE
                    toSave.add(existingLink);
                }
            } else {
                Attraction attraction = newAttractionsById.get(attractionId);
                toSave.add(new StationAttractions(station, attraction, distance));
            }
        }
        if (!toSave.isEmpty()) {
            stationAttractionsRepository.saveAll(toSave);
        }
    }
    private void updateStationMedias(Station station, StationMediasRequest mediaRequest) {
        // id старых медиа ДО очистки join-таблицы station_medias
        List<Long> oldMediaIds = station.getMedias() == null
                ? List.of()
                : station.getMedias().stream().map(Media::getId).toList();

        Set<Media> newMedias = buildMedias(mediaRequest); // переиспользуем твой приватный метод

        if (station.getMedias() == null) {
            station.setMedias(new HashSet<>());
        } else {
            station.getMedias().clear();
        }
        station.getMedias().addAll(newMedias);

        // join-таблица должна обновиться ДО удаления осиротевших медиа
        // (@Modifying deleteOrphansByIds по умолчанию не делает flush сам)
        stationRepository.flush();

        if (!oldMediaIds.isEmpty()) {
            mediaRepository.deleteOrphansByIds(oldMediaIds);
        }
    }

    private void updateCellTowers(Station station, List<CellTowerRequest> towerRequests) {
        // Полная замена: старые вышки удаляем, новые вставляем.
        // У Station нет каскада на CellTower, поэтому пишем через CellTowerRepository.
        cellTowerRepository.deleteByStationId(station.getId());

        if (towerRequests == null || towerRequests.isEmpty()) {
            return;
        }

        List<CellTower> towers = towerRequests.stream()
                .map(req -> new CellTower(
                        req.cid(),
                        req.lac(),
                        req.mcc(),
                        req.mnc(),
                        req.radio(),
                        station))
                .toList();
        cellTowerRepository.saveAll(towers);
    }

    private void createAttractionLinks(Station station, List<StationAttractionLinkRequest> attractions) {
        if (attractions == null || attractions.isEmpty()) {
            return;
        }

        // Один SELECT по всем id сразу
        List<Long> attractionIds = attractions.stream()
                .map(StationAttractionLinkRequest::attractionId)
                .toList();

        Map<Long, Attraction> attractionsById = attractionRepository.findAllById(attractionIds).stream()
                .collect(Collectors.toMap(Attraction::getId, a -> a));

        List<StationAttractions> links = attractions.stream()
                .map(req -> new StationAttractions(
                        station,
                        attractionsById.get(req.attractionId()),
                        req.distance()))
                .toList();

        stationAttractionsRepository.saveAll(links);
    }
    @Transactional
    public StationCreatedResponse addStation(AddStationRequest request) {
        // 1. Проверка уникальности (name, branch)
        stationRepository.findByNameAndBranch(request.name(), request.branch())
                .ifPresent(s -> {
                    throw new ResourceNotFoundException(
                            "Станция уже существует: ", request.name() + " (" + request.branch() + ")");
                });

        // 2. Создаём саму станцию.
        Station station = new Station(
                "",                       // address
                new ArrayList<>(),        // extraServices
                request.description(),
                request.branch(),
                request.name()
        );

        // 3. Готовим коллекцию медиа.
        station.setMedias(buildMedias(request.media()));

        // 4. Сохраняем станцию (вместе с медиа по каскаду) и получаем сгенерированный id.
        Station saved = stationRepository.save(station);

        // 5. Сохраняем вышки.
        if (request.cellTowers() != null && !request.cellTowers().isEmpty()) {
            List<CellTower> towers = request.cellTowers().stream()
                    .map(req -> new CellTower(
                            req.cid(),
                            req.lac(),
                            req.mcc(),
                            req.mnc(),
                            req.radio(),
                            saved))
                    .toList();
            cellTowerRepository.saveAll(towers);
        }


// 6. Сохраняем связи с достопримечательностями.
//    У Station OneToMany на StationAttractions с CascadeType.ALL, но обратная коллекция
//    в saved сейчас null/не инициализирована — поэтому пишем явно через репозиторий.
        createAttractionLinks(saved, request.stationAttractions());

        return new StationCreatedResponse(saved.getId(), saved.getName(), saved.getBranch());

    }
    private Set<Media> buildMedias(StationMediasRequest mediaRequest) {
        if (mediaRequest == null) {
            return new HashSet<>();
        }
        Set<Media> medias = new HashSet<>();
        addByType(medias, mediaRequest.photoUrls(), MediaType.PHOTO);
        addByType(medias, mediaRequest.videoUrls(), MediaType.VIDEO);
        addByType(medias, mediaRequest.audioUrls(), MediaType.AUDIO);
        return medias;
    }

    private void addByType(Set<Media> target, List<String> urls, MediaType type) {
        if (urls == null) return;
        for (String url : urls) {
            if (url == null || url.isBlank()) continue;
            // Подразумевается, что у Media есть конструктор Media(MediaType, String urlRef).
            // Если конструктор другой — замени на сеттеры/билдер.
            target.add(new Media(type, null,url));
        }
    }

    public StationResponse getStationByNameAndBranch(String name, String branch) {
        Station station = stationRepository.findByNameAndBranch(name, branch)
                .orElseThrow(() -> new ResourceNotFoundException("Станция не найдена:", name));

        Map<MediaType, List<String>> mediaByType =
                mediaRepository.findAllStationMedias(station.getId()).stream()
                        .collect(Collectors.groupingBy(
                                MediaProjection::type,
                                Collectors.mapping(MediaProjection::urlRef, Collectors.toList())));

        Pageable topFive = PageRequest.of(0, 5, Sort.by("distance"));
        List<StationAttractions> stationAttractions =
                stationAttractionsRepository.findStationAttractionsByStation(station.getId(), topFive);

        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getBranch(),
                station.getAddress(),
                station.getDescription(),
                mediaByType.getOrDefault(MediaType.PHOTO, List.of()),
                mediaByType.getOrDefault(MediaType.VIDEO, List.of()),
                mediaByType.getOrDefault(MediaType.AUDIO, List.of()),
                station.getExtraServices().stream().map(ExtraService::getName).toList(),
                toAttractionResponses(stationAttractions));
    }

    private List<AttractionResponse> toAttractionResponses(List<StationAttractions> stationAttractions) {
        List<Long> attractionIds = stationAttractions.stream()
                .map(sa -> sa.getAttraction().getId())
                .toList();

        Map<Long, String> photoByAttractionId = attractionIds.isEmpty()
                ? Map.of()
                : mediaRepository.findPhotosByAttractionIds(attractionIds, MediaType.PHOTO).stream()
                  .collect(Collectors.toMap(
                          AttractionPhoto::attractionId,
                          AttractionPhoto::urlRef,
                          (first, second) -> first));

        return stationAttractions.stream()
                .map(sa -> {
                    Attraction attraction = sa.getAttraction();
                    return new AttractionResponse(
                            attraction.getId(),
                            sa.getDistance(),
                            attraction.getName(),
                            attraction.getPrice(),
                            photoByAttractionId.get(attraction.getId()));
                })
                .toList();
    }


    public StationsResponse getAllStations() {
        List<Station> stations=stationRepository.findAll();
        return new StationsResponse(
        stations.stream()
                .map(station -> new StationNameAndBranch(
                        station.getId(),
                        station.getName(),
                        station.getBranch()))
                .toList());
    }
    @Transactional
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Станция не найдена:", String.valueOf(id)));

        // CellTower не имеет каскада со стороны Station — чистим явно,
        // до удаления самой станции, чтобы не нарушить FK.
        cellTowerRepository.deleteByStationId(id);

        // Дальше JPA-каскад сам разберётся:
        //  - station_medias + medias    (ManyToMany cascade ALL)
        //  - station_attractions        (OneToMany cascade ALL) — БЕЗ удаления Attraction
        //  - extra_services             (OneToMany cascade ALL + orphanRemoval)
        stationRepository.delete(station);
    }
}

