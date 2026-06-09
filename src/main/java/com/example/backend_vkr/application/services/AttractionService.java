package com.example.backend_vkr.application.services;

import com.example.backend_vkr.application.dto.*;

import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.Media;
import com.example.backend_vkr.domain.Station;
import com.example.backend_vkr.domain.StationAttractions;
import com.example.backend_vkr.domain.enums.MediaType;
import com.example.backend_vkr.data.JPAAttractionRepository;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationAttractionsRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import com.example.backend_vkr.domain.repositories.AttractionRepository;
import com.example.backend_vkr.domain.repositories.MediaRepository;
import com.example.backend_vkr.domain.repositories.StationAttractionsRepository;
import com.example.backend_vkr.domain.repositories.StationRepository;
import com.example.backend_vkr.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttractionService {
    private final StationRepository stationRepository;
    private final MediaRepository mediaRepository;
    private final StationAttractionsRepository stationAttractionsRepository;
    private final AttractionRepository attractionRepository;

    @Autowired
    public AttractionService(StationRepository stationRepository, MediaRepository mediaRepository, StationAttractionsRepository stationAttractionsRepository, AttractionRepository attractionRepository) {
        this.stationRepository = stationRepository;
        this.mediaRepository = mediaRepository;
        this.stationAttractionsRepository = stationAttractionsRepository;
        this.attractionRepository = attractionRepository;
    }

    public PagedResponse<AttractionResponse> getStationAttractions(Long stationId, int page, int size) {
        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Станция не найдена:", stationId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("distance"));
        Page<StationAttractions> attractionsPage =
                stationAttractionsRepository.findAllStationAttractionsByStationPage(stationId, pageable);

        List<Long> attractionIds = attractionsPage.stream()
                .map(sa -> sa.getAttraction().getId())
                .toList();

        Map<Long, String> photoByAttractionId = attractionIds.isEmpty()
                ? Map.of()
                : mediaRepository.findPhotosByAttractionIds(attractionIds, MediaType.PHOTO).stream()
                  .collect(Collectors.toMap(
                          AttractionPhoto::attractionId,
                          AttractionPhoto::urlRef,
                          (first, second) -> first));

        List<AttractionResponse> content = attractionsPage.stream()
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

        return new PagedResponse<>(
                content,
                attractionsPage.getNumber(),
                attractionsPage.getSize(),
                attractionsPage.getTotalElements(),
                attractionsPage.getTotalPages(),
                attractionsPage.isLast());
    }

    public AttractionInfoResponse findAttractionById(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));
        List<String> photos = mediaRepository.findAllAttractionMediasByType(MediaType.PHOTO, id);
        List<String> videos = mediaRepository.findAllAttractionMediasByType(MediaType.VIDEO, id);
        List<String> audios = mediaRepository.findAllAttractionMediasByType(MediaType.AUDIO, id);
        List<StationAttractions> stationAttractions = stationAttractionsRepository.findStationAttractionsByAttraction(id);
        List<StationAttractionRequest> stationAttractionRequests = stationAttractions.stream().map(
                sa -> new StationAttractionRequest(
                        sa.getStation().getName(),
                        sa.getStation().getBranch(),
                        sa.getDistance())).toList();

        return new AttractionInfoResponse(id,
                attraction.getName(),
                attraction.getPhoneNumber(),
                attraction.getEmail(),
                attraction.getAddress(),
                attraction.getWorkingHours(),
                attraction.getDescription(),
                attraction.getPrice(), attraction.getUrlRef(),
                photos,
                videos,
                audios,
                stationAttractionRequests);
    }
    @Transactional
    public AttractionInfoResponse updateAttraction(Long id, AttractionRequest request) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));

        // 1) Простые поля
        attraction.setName(request.name());
        attraction.setPhoneNumber(request.phoneNumber());
        attraction.setEmail(request.email());
        attraction.setAddress(request.address());
        attraction.setDescription(request.description());
        attraction.setWorkingHours(request.workingHours());
        attraction.setPrice(request.price());
        attraction.setUrlRef(request.urlRef());

        // 2) Запоминаем ID старых медиа ДО очистки
        List<Long> oldMediaIds = attraction.getMedias() == null
                ? List.of()
                : attraction.getMedias().stream().map(Media::getId).toList();

        // 3) Очищаем join-таблицу attraction_medias
        if (attraction.getMedias() == null) {
            attraction.setMedias(new HashSet<>());
        } else {
            attraction.getMedias().clear();
        }

        // 4) Добавляем новые медиа
        Set<Media> newMedias = request.medias() == null
                ? Set.of()
                :  request.medias().stream()
                .map(req -> {
                    Media media = new Media(req.type(), null, req.urlRef());
                    media.setAttractions(Set.of(attraction));
                    return media;
                })
                .collect(Collectors.toSet());
        attraction.getMedias().addAll(newMedias);

        // 5) Сбрасываем изменения в БД, чтобы join-таблица обновилась ДО проверки сирот
        attractionRepository.flush();

        // 6) Удаляем осиротевшие медиа
        if (!oldMediaIds.isEmpty()) {
            mediaRepository.deleteOrphansByIds(oldMediaIds);
        }

        // 7) Связи со станциями
        List<StationAttractions> oldLinks =
                stationAttractionsRepository.findStationAttractionsByAttraction(id);
        stationAttractionsRepository.deleteAll(oldLinks);
        stationAttractionsRepository.flush();

        List<StationAttractions> newLinks = request.stationAttractions().stream()
                .map(req -> {
                    Station station = stationRepository.findByNameAndBranch(req.stationName(), req.branch())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Station", req.branch() + " " + req.stationName()));
                    return new StationAttractions(station, attraction, req.distance());
                })
                .toList();
        stationAttractionsRepository.saveAll(newLinks);

        return findAttractionById(id);
    }
    @Transactional
    public AttractionCreatedResponse addAttraction(AttractionRequest request) {
        List<StationAttractions> stationLinks = request.stationAttractions().stream()
                .map(req -> {
                    Station station = stationRepository.findByNameAndBranch(req.stationName(), req.branch())
                            .orElseThrow(() -> new ResourceNotFoundException("Station", req.branch() + " " + req.stationName()));
                    return new StationAttractions(station, null, req.distance());
                })
                .toList();

        Attraction attraction = new Attraction(
                request.name(), request.phoneNumber(), request.email(),
                request.address(), request.description(), request.workingHours(),
                request.price(), request.urlRef()
        );
        Set<Media> mediaSet = request.medias().stream()
                .map(req -> {
                    Media media = new Media(req.type(), null, req.urlRef());
                    media.setAttractions(Set.of(attraction));
                    return media;
                })
                .collect(Collectors.toSet());

        attraction.setMedias(mediaSet);
        attractionRepository.save(attraction);
        System.out.println(mediaSet);

        stationLinks.forEach(link -> link.setAttraction(attraction));
        stationAttractionsRepository.saveAll(stationLinks);

        return new AttractionCreatedResponse(attraction.getId());
    }

    @Transactional
    public void deleteAttraction(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));

        List<Long> mediaIds = attraction.getMedias() == null
                ? List.of()
                : attraction.getMedias().stream().map(Media::getId).toList();

        // Удаляем саму достопримечательность (cascade удалит записи из attraction_medias)
        attractionRepository.delete(attraction);
        attractionRepository.flush();

        // Чистим сирот
        if (!mediaIds.isEmpty()) {
            mediaRepository.deleteOrphansByIds(mediaIds);
        }
    }

    public AttractionsResponse getAllAttractions() {
        List<Attraction> attractions = attractionRepository.findAllWithMedias();

        return new AttractionsResponse(attractions.stream().map(attraction -> {
            Map<MediaType, List<String>> mediasByType = attraction.getMedias() == null
                    ? Map.of()
                    : attraction.getMedias().stream()
                      .collect(Collectors.groupingBy(
                              Media::getType,
                              Collectors.mapping(Media::getUrlRef, Collectors.toList())
                      ));

            return new AttractionInfoResponse(
                    attraction.getId(),
                    attraction.getName(),
                    attraction.getPhoneNumber(),
                    attraction.getEmail(),
                    attraction.getAddress(),
                    attraction.getWorkingHours(),
                    attraction.getDescription(),
                    attraction.getPrice(),
                    attraction.getUrlRef(),
                    mediasByType.getOrDefault(MediaType.PHOTO, List.of()),
                    mediasByType.getOrDefault(MediaType.VIDEO, List.of()),
                    mediasByType.getOrDefault(MediaType.AUDIO, List.of()),
                    List.of()
            );
        }).toList());
    }
}