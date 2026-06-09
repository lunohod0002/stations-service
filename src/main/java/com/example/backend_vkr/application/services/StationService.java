package com.example.backend_vkr.application.services;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_vkr.application.dto.*;
import com.example.backend_vkr.domain.*;
import com.example.backend_vkr.domain.enums.MediaType;

import com.example.backend_vkr.domain.exceptions.ResourceNotFoundException;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import com.example.backend_vkr.domain.repositories.CellTowerRepository;
import com.example.backend_vkr.domain.repositories.MediaRepository;
import com.example.backend_vkr.domain.repositories.StationAttractionsRepository;
import com.example.backend_vkr.domain.repositories.StationRepository;
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

    @Autowired
    public StationService(StationRepository stationRepository,
                          MediaRepository mediaRepository,
                          StationAttractionsRepository stationAttractionsRepository,
                          CellTowerRepository cellTowerRepository) {
        this.stationRepository = stationRepository;
        this.mediaRepository = mediaRepository;
        this.stationAttractionsRepository = stationAttractionsRepository;
        this.cellTowerRepository = cellTowerRepository;
    }
    @Transactional
    public StationCreatedResponse addStation(AddStationRequest request) {
        // 1. Проверка уникальности (name, branch) — на бд constraint, но дружелюбное сообщение лучше
        stationRepository.findByNameAndBranch(request.name(), request.branch())
                .ifPresent(s -> {
                    throw new ResourceNotFoundException(
                            "Станция уже существует: ", request.name() + " (" + request.branch() + ")");
                });

        // 2. Создаём саму станцию.
        //    address и extraServices не пришли в запросе — подставляем безопасные дефолты,
        //    чтобы не падал NOT NULL constraint у address.
        Station station = new Station(
                "",                       // address
                new ArrayList<>(),        // extraServices
                request.description(),
                request.branch(),
                request.name()
        );

        // 3. Готовим коллекцию медиа. ManyToMany с CascadeType.ALL сохранит их вместе со станцией.
        station.setMedias(buildMedias(request.media()));

        // 4. Сохраняем станцию (вместе с медиа по каскаду) и получаем сгенерированный id.
        Station saved = stationRepository.save(station);

        // 5. Сохраняем вышки. У Station нет обратной коллекции CellTower —
        //    каскад здесь не сработает, поэтому пишем через CellTowerRepository.
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

