package com.example.backend_vkr.application.services;

import com.example.backend_vkr.application.dto.AttractionResponse;
import com.example.backend_vkr.application.dto.StationResponse;
import com.example.backend_vkr.domain.*;
import com.example.backend_vkr.domain.enums.MediaType;

import com.example.backend_vkr.domain.exceptions.ResourceNotFoundException;
import com.example.backend_vkr.data.JPAAttractionRepository;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    private final JPAStationRepository JPAStationRepository;
    private final JPAMediaRepository JPAMediaRepository;
    private final JPAAttractionRepository JPAAttractionRepository;
    @Autowired
    public StationService(JPAStationRepository JPAStationRepository, JPAMediaRepository JPAMediaRepository, JPAAttractionRepository JPAAttractionRepository) {
        this.JPAStationRepository = JPAStationRepository;
        this.JPAMediaRepository = JPAMediaRepository;
        this.JPAAttractionRepository = JPAAttractionRepository;
    }
    public StationResponse getStationByNameAndBranch(String name, String branch) {
        Station station = JPAStationRepository.findByNameAndBranch(name, branch)
                .orElseThrow(() -> new ResourceNotFoundException("Станция не найдена:", name));
        List<String> photos= JPAMediaRepository.findAllStationMediasByType(MediaType.PHOTO,station.getId());
        List<String> videos= JPAMediaRepository.findAllStationMediasByType(MediaType.VIDEO,station.getId());
        List<String> audios= JPAMediaRepository.findAllStationMediasByType(MediaType.AUDIO,station.getId());
        Pageable pageable = PageRequest.of(0, 5, Sort.by("distance"));
        Page<StationAttractions> attractions = JPAAttractionRepository.findAllStationAttractions(  station.getId(),pageable);
        List<AttractionResponse> stationAttractions = attractions.stream().map(stationAttraction -> {
            Attraction attraction = stationAttraction.getAttraction();
            String photoUrl = attraction.getMedias() == null ? null : attraction.getMedias().stream()
                                                                      .filter(media -> media.getType() == MediaType.PHOTO)
                                                                      .findFirst()
                                                                      .map(Media::getUrlRef)
                                                                      .orElse(null);
            return new AttractionResponse(
                    attraction.getId(),
                    stationAttraction.getDistance(),
                    attraction.getName(),
                    attraction.getPrice(),
                    photoUrl
            );
        }).toList();
        return new StationResponse(station.getId(), station.getName(), station.getBranch(), station.getAddress(), station.getDescription(), photos,videos,audios,station.getExtraServices().stream().map(ExtraService::getName).toList(),stationAttractions);
    }



}
