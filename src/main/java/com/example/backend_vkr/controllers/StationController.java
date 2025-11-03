package com.example.backend_vkr.controllers;


import com.example.backend_vkr.models.AttractionInfoResponse;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.StationResponse;
import com.example.backend_vkr.services.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class StationController implements StationApi {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;

    }


    @Override
    public StationResponse getStationInfo(Long id) {
        return stationService.findStationById(id);

    }

    @Override
    public List<AttractionResponse> getStationAttractions(Long id) {
        return List.of();
    }

    @Override
    public AttractionInfoResponse getAttraction(Long id) {
        return stationService.findAttractionById(id);
    }
}
