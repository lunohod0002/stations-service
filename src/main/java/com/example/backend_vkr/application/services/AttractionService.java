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
import com.example.backend_vkr.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttractionService {
    private final JPAMediaRepository JPAMediaRepository;
    private final JPAAttractionRepository JPAAttractionRepository;
    private final JPAStationAttractionsRepository stationAttractionRepository;
    private final JPAStationRepository JPAStationRepository;
    @Autowired
    public AttractionService(JPAMediaRepository JPAMediaRepository, JPAStationAttractionsRepository stationAttractionRepository, JPAAttractionRepository JPAAttractionRepository, JPAStationRepository JPAStationRepository) {
        this.JPAMediaRepository = JPAMediaRepository;
        this.JPAAttractionRepository = JPAAttractionRepository;
        this.stationAttractionRepository = stationAttractionRepository;
        this.JPAStationRepository = JPAStationRepository;
    }
    public PagedResponse<AttractionResponse> getStationAttractions(Long stationId, int page, int size) {
        if (!JPAStationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Станция не найдена:", stationId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("distance"));
        Page<StationAttractions> attractionsPage =
                JPAAttractionRepository.findAllStationAttractions(stationId, pageable);

        List<Long> attractionIds = attractionsPage.stream()
                .map(sa -> sa.getAttraction().getId())
                .toList();

        Map<Long, String> photoByAttractionId = attractionIds.isEmpty()
                ? Map.of()
                : JPAMediaRepository.findPhotosByAttractionIds(attractionIds, MediaType.PHOTO).stream()
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
        Attraction attraction = JPAAttractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));
        List<String> photos = JPAMediaRepository.findAllAttractionMediasByType(MediaType.PHOTO, id);
        List<String> videos = JPAMediaRepository.findAllAttractionMediasByType(MediaType.VIDEO, id);
        List<String> audios = JPAMediaRepository.findAllAttractionMediasByType(MediaType.AUDIO, id);

        return new AttractionInfoResponse(id, attraction.getName(),attraction.getPhoneNumber() ,attraction.getEmail(),attraction.getAddress(), attraction.getWorkingHours(), attraction.getDescription(), attraction.getPrice(), attraction.getUrlRef(), photos, videos, audios);
    }

    @Transactional
    public AttractionCreatedResponse addAttraction(AttractionRequest request) {
        List<StationAttractions> stationLinks = request.stationAttractions().stream()
                .map(req -> {
                    Station station = JPAStationRepository.findByNameAndBranch(req.stationName(), req.branch())
                            .orElseThrow(() -> new ResourceNotFoundException("Station", req.branch() + " " + req.stationName()));
                    return new StationAttractions(station, null, req.distance());
                })
                .toList();

        Attraction attraction = new Attraction(
                request.name(), request.phoneNumber(), request.email(),
                request.address(), request.description(), request.workingHours(),
                request.price(), request.urlRef()
        );
        System.out.println(request.medias());
        Set<Media> mediaSet = request.medias().stream()
                .map(req -> {
                    Media media = new Media(req.type(), null, req.urlRef());
                    media.setAttractions(Set.of(attraction));
                    return media;
                })
                .collect(Collectors.toSet());

        attraction.setMedias(mediaSet);
       JPAAttractionRepository.save(attraction);
        JPAMediaRepository.saveAll(mediaSet);

        stationLinks.forEach(link -> link.setAttraction(attraction));
        stationAttractionRepository.saveAll(stationLinks);

        return new AttractionCreatedResponse(attraction.getId());
    }

}
