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
public class AttractionService {
    private final MediaRepository mediaRepository;
    private final AttractionRepository attractionRepository;
    @Autowired
    public AttractionService(MediaRepository mediaRepository, AttractionRepository attractionRepository) {
        this.mediaRepository = mediaRepository;
        this.attractionRepository = attractionRepository;
    }

    public List<AttractionResponse> getStationAttractions(Long stationId) {
//        Attraction attraction =  attractionRepository.findById(stationId)
//                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", stationId);
        return null;
    }
    public AttractionInfoResponse findAttractionById(Long id) {
        Attraction attraction =  attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Достопримечательность не найдена:", id));
        return null;
    }



}
