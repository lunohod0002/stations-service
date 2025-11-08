package com.example.backend_vkr.services;

import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.Station;
import com.example.backend_vkr.entities.StationAttractions;
import com.example.backend_vkr.entities.enums.MediaType;
import com.example.backend_vkr.exception.ResourceNotFoundException;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.StationResponse;
import com.example.backend_vkr.repositories.AttractionRepository;
import com.example.backend_vkr.repositories.MediaRepository;
import com.example.backend_vkr.repositories.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;
    private final MediaRepository mediaRepository;
    private final AttractionRepository attractionRepository;
    @Autowired
    public StationService(StationRepository stationRepository, MediaRepository mediaRepository, AttractionRepository attractionRepository) {
        this.stationRepository = stationRepository;
        this.mediaRepository = mediaRepository;
        this.attractionRepository = attractionRepository;
    }


    public StationResponse getStationByNameAndBranch(String name, String branch) {
        Station station = stationRepository.findByNameAndBranch(name, branch)
                .orElseThrow(() -> new ResourceNotFoundException("Станция не найдена:", name));
        List<String> photos=mediaRepository.findAllStationMediasByType(MediaType.PHOTO,station.getId());
        List<String> videos=mediaRepository.findAllStationMediasByType(MediaType.VIDEO,station.getId());
        List<String> audios=mediaRepository.findAllStationMediasByType(MediaType.AUDIO,station.getId());
        Pageable pageable = PageRequest.of(0, 5, Sort.by("distance"));

        Page<StationAttractions> attractions = attractionRepository.findAllStationAttractions(  station.getId(),pageable);
        List<AttractionResponse> attractionResponses = attractions.stream().map(
                stationAttraction -> new AttractionResponse(stationAttraction.getAttraction().getId(),stationAttraction.getDistance(),stationAttraction.getAttraction().getName(),stationAttraction.getAttraction().getPrice(),stationAttraction.getAttraction().getAddress())).toList() ;
        return new StationResponse(station.getId(), station.getName(), station.getBranch(), station.getAddress(), station.getBuiltAt(), station.getDescription(), photos,videos,audios,attractionResponses);
    }



}
