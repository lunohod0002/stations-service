package com.example.backend_vkr.services;

import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.Station;
import com.example.backend_vkr.entities.enums.MediaType;
import com.example.backend_vkr.exception.ResourceNotFoundException;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.StationResponse;
import com.example.backend_vkr.repositories.AttractionRepository;
import com.example.backend_vkr.repositories.MediaRepository;
import com.example.backend_vkr.repositories.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public StationResponse findStationById(Long id) {
        Station station = stationRepository.findStationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Станция не найдена:", id));
        List<String> photos=mediaRepository.findAllStationMediasByType(MediaType.PHOTO,id);
        List<String> videos=mediaRepository.findAllStationMediasByType(MediaType.VIDEO,id);
        List<String> audios=mediaRepository.findAllStationMediasByType(MediaType.AUDIO,id);
        List<Attraction> attractions = attractionRepository.findAllStationAttractions(id);
        List<AttractionResponse> attractionResponses = attractions.stream().map(
                attraction -> new AttractionResponse(attraction.getId(),attraction.getName(),attraction.getPrice(),attraction.getAddress())).toList() ;
        return new StationResponse(station.getId(), station.getName(), station.getBranch(), station.getAddress(), station.getBuiltAt(), station.getDescription(), photos,videos,audios,attractionResponses);
    }



}
