package com.example.backend_vkr.services;

import com.example.backend_vkr.dto.*;
import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.Media;
import com.example.backend_vkr.entities.Station;
import com.example.backend_vkr.entities.StationAttractions;

import com.example.backend_vkr.entities.enums.MediaType;
import com.example.backend_vkr.exceptions.ResourceNotFoundException;
import com.example.backend_vkr.repositories.AttractionRepository;
import com.example.backend_vkr.repositories.MediaRepository;
import com.example.backend_vkr.repositories.StationAttractionsRepository;
import com.example.backend_vkr.repositories.StationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class AttractionService {
    private final MediaRepository mediaRepository;
    private final AttractionRepository attractionRepository;
    private final StationAttractionsRepository stationAttractionRepository;

    private final StationRepository stationRepository;

    @Autowired
    public AttractionService(MediaRepository mediaRepository, StationAttractionsRepository stationAttractionRepository, AttractionRepository attractionRepository, StationRepository stationRepository) {
        this.mediaRepository = mediaRepository;
        this.attractionRepository = attractionRepository;
        this.stationAttractionRepository = stationAttractionRepository;
        this.stationRepository = stationRepository;
    }

    public PagedResponse<AttractionResponse> getStationAttractions(Long stationId, int page, int size) {
        stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Станция не найдена:", stationId));
        Pageable pageable = PageRequest.of(page, size, Sort.by("distance"));
        Page<StationAttractions> attractions = attractionRepository.findAllStationAttractions(stationId, pageable);


        List<AttractionResponse> pagedContent = attractions.stream().map(
                stationAttraction -> new AttractionResponse(stationAttraction.getAttraction().getId(), stationAttraction.getDistance(), stationAttraction.getAttraction().getName(), stationAttraction.getAttraction().getPrice(), stationAttraction.getAttraction().getAddress())).toList();
        return new PagedResponse<>(pagedContent, page, size, attractions.getTotalElements(), attractions.getTotalPages(), page >= attractions.getTotalPages() - 1);

    }

    public AttractionInfoResponse findAttractionById(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));
        List<String> photos = mediaRepository.findAllAttractionMediasByType(MediaType.PHOTO, id);
        List<String> videos = mediaRepository.findAllAttractionMediasByType(MediaType.VIDEO, id);
        List<String> audios = mediaRepository.findAllAttractionMediasByType(MediaType.AUDIO, id);

        return new AttractionInfoResponse(id, attraction.getName(), attraction.getAddress(), attraction.getWorkingHours(), attraction.getDescription(), attraction.getPrice(), attraction.getUrlRef(), photos, videos, audios);
    }

    @Transactional
    public AttractionCreatedResponse addAttraction(AttractionRequest request) {
        List<StationAttractions> stationLinks = request.stationAttractions().stream()
                .map(req -> {
                    Station station = stationRepository.findByNameAndBranch(req.name(), req.branch())
                            .orElseThrow(() -> new ResourceNotFoundException("Station", req.branch() + " " + req.name()));
                    return new StationAttractions(station, null, req.duration());
                })
                .toList();

        Attraction attraction = new Attraction(
                request.name(), request.phoneNumber(), request.email(),
                request.address(), request.description(), request.workingHours(),
                request.price(), request.urlRef()
        );

        Set<Media> mediaSet = request.medias().stream()
                .map(req -> {
                    Media media = new Media(req.type(), req.name(), req.source(), req.urlRef());
                    media.setAttractions(Set.of(attraction));
                    return media;
                })
                .collect(Collectors.toSet());

        attraction.setMedias(mediaSet);
       attractionRepository.save(attraction);
        mediaRepository.saveAll(mediaSet);

        stationLinks.forEach(link -> link.setAttraction(attraction));
        stationAttractionRepository.saveAll(stationLinks);

        return new AttractionCreatedResponse(attraction.getId());
    }

}
