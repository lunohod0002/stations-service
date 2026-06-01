package com.example.backend_vkr.application.services;

import com.example.backend_vkr.application.dto.AttractionPhoto;
import com.example.backend_vkr.application.dto.AttractionResponse;
import com.example.backend_vkr.application.dto.MediaProjection;
import com.example.backend_vkr.application.dto.StationResponse;
import com.example.backend_vkr.domain.*;
import com.example.backend_vkr.domain.enums.MediaType;

import com.example.backend_vkr.domain.exceptions.ResourceNotFoundException;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import com.example.backend_vkr.domain.repositories.MediaRepository;
import com.example.backend_vkr.domain.repositories.StationAttractionsRepository;
import com.example.backend_vkr.domain.repositories.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepository stationRepository;
    private final MediaRepository mediaRepository;
    private final StationAttractionsRepository stationAttractionsRepository;

    @Autowired
    public StationService(StationRepository stationRepository, MediaRepository mediaRepository, StationAttractionsRepository stationAttractionsRepository) {
        this.stationRepository = stationRepository;
        this.mediaRepository = mediaRepository;
        this.stationAttractionsRepository = stationAttractionsRepository;
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
                stationAttractionsRepository.findStationAttractions(station.getId(), topFive);

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


}
