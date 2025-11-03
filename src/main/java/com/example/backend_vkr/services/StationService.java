package com.example.backend_vkr.services;

import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.Station;
import com.example.backend_vkr.entities.enums.MediaType;
import com.example.backend_vkr.exception.ResourceNotFoundException;
import com.example.backend_vkr.models.AttractionInfoResponse;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.PagedResponse;
import com.example.backend_vkr.models.StationResponse;
import com.example.backend_vkr.repositories.AttractionRepository;
import com.example.backend_vkr.repositories.MediaRepository;
import com.example.backend_vkr.repositories.StationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Станция не найдена:", id));
        List<String> photos=mediaRepository.findAllStationMediasByTpe(MediaType.PHOTO,id);
        List<String> videos=mediaRepository.findAllStationMediasByTpe(MediaType.VIDEO,id);
        List<String> audios=mediaRepository.findAllStationMediasByTpe(MediaType.AUDIO,id);
        List<Attraction> attractions = attractionRepository.findAllStationAttractions(id);
        List<AttractionResponse> attractionResponses = attractions.stream().map(
                attraction -> new AttractionResponse(attraction.getId(),attraction.getName(),attraction.getPrice(),attraction.getAddress())).toList() ;
        return new StationResponse(station.getId(), station.getName(), station.getBranch(), station.getAddress(), station.getBuiltAt(), station.getDescription(), photos,videos,audios,attractionResponses);
    }
    public AttractionInfoResponse findAttractionById(Long id) {
        Attraction attraction =  attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));
        return null;
    }



}
