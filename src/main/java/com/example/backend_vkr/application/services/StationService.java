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

        stationRepository.findByNameAndBranch(request.name(), request.branch())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ResourceNotFoundException(
                            "Станция уже существует: ", request.name() + " (" + request.branch() + ")");
                });

        station.setName(request.name());
        station.setBranch(request.branch());
        station.setDescription(request.description());
        if (request.address() != null) {
            station.setAddress(request.address());
        }

        updateStationMedias(station, request.media());

        updateCellTowers(station, request.cellTowers());

        syncAttractionLinks(station, request.attractions());

        return getStationByNameAndBranch(station.getName(), station.getBranch());
    }

    private void syncAttractionLinks(Station station, List<StationAttractionLinkRequest> incoming) {
        Map<Long, Integer> incomingByAttractionId = new LinkedHashMap<>();
        if (incoming != null) {
            for (StationAttractionLinkRequest req : incoming) {
                incomingByAttractionId.put(req.attractionId(), req.distance());
            }
        }

        List<StationAttractions> existing =
                stationAttractionsRepository.findByStationId(station.getId());
        Map<Long, StationAttractions> existingByAttractionId = existing.stream()
                .collect(Collectors.toMap(sa -> sa.getAttraction().getId(), sa -> sa));

        List<StationAttractions> toDelete = existing.stream()
                .filter(sa -> !incomingByAttractionId.containsKey(sa.getAttraction().getId()))
                .toList();
        if (!toDelete.isEmpty()) {
            stationAttractionsRepository.deleteAll(toDelete);
            stationAttractionsRepository.flush();
        }

        if (incomingByAttractionId.isEmpty()) {
            return;
        }

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

        List<StationAttractions> toSave = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : incomingByAttractionId.entrySet()) {
            Long attractionId = entry.getKey();
            int distance = entry.getValue();

            StationAttractions existingLink = existingByAttractionId.get(attractionId);
            if (existingLink != null) {
                if (!Objects.equals(existingLink.getDistance(), distance)) {
                    existingLink.setDistance(distance);
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
        List<Long> oldMediaIds = station.getMedias() == null
                ? List.of()
                : station.getMedias().stream().map(Media::getId).toList();

        Set<Media> newMedias = buildMedias(mediaRequest);

        if (station.getMedias() == null) {
            station.setMedias(new HashSet<>());
        } else {
            station.getMedias().clear();
        }
        station.getMedias().addAll(newMedias);

        stationRepository.flush();

        if (!oldMediaIds.isEmpty()) {
            mediaRepository.deleteOrphansByIds(oldMediaIds);
        }
    }

    private void updateCellTowers(Station station, List<CellTowerRequest> towerRequests) {
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

    @Transactional
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

        List<CellTowerRequest> cellTowers = cellTowerRepository.findAllByStationId(station.getId())
                .stream()
                .map(tower -> new CellTowerRequest(
                        tower.getCid(),
                        tower.getLac(),
                        tower.getMcc(),
                        tower.getMnc(),
                        tower.getRadio()
                ))
                .toList();

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
                toAttractionResponses(stationAttractions),
                cellTowers
        );
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
        List<Station> stations = stationRepository.findAllWithMedias();

        List<CellTower> allTowers = cellTowerRepository.findAllWithStations();

        Map<Long, List<CellTower>> towersByStationId = allTowers.stream()
                .filter(tower -> tower.getStation() != null)
                .collect(Collectors.groupingBy(tower -> tower.getStation().getId()));

        return new StationsResponse(
                stations.stream()
                        .map(station -> new StationNameAndBranch(
                                station.getId(),
                                station.getName(),
                                station.getBranch(),
                                station.getLatitude(),
                                station.getLongitude(),
                                towersByStationId.getOrDefault(station.getId(), Collections.emptyList())
                                        .stream()
                                        .map(tower -> new CellTowerRequest(
                                                tower.getCid(),
                                                tower.getLac(),
                                                tower.getMcc(),
                                                tower.getMnc(),
                                                tower.getRadio()
                                        ))
                                        .toList()
                        ))
                        .toList()
        );
    }
    private Map<MediaType, List<String>> mapMedias(Set<Media> medias) {
        if (medias == null || medias.isEmpty()) {
            return Map.of();
        }

        return medias.stream()
                .collect(Collectors.groupingBy(
                        Media::getType,
                        Collectors.mapping(Media::getUrlRef, Collectors.toList())
                ));
    }
    @Transactional
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Станция не найдена:", String.valueOf(id)));

        cellTowerRepository.deleteByStationId(id);

        stationRepository.delete(station);
    }
}
