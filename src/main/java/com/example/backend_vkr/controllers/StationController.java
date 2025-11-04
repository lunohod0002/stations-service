package com.example.backend_vkr.controllers;


import com.example.backend_vkr.models.AttractionInfoResponse;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.PagedResponse;
import com.example.backend_vkr.models.StationResponse;
import com.example.backend_vkr.services.AttractionService;
import com.example.backend_vkr.services.StationService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StationController implements StationApi {

    private final StationService stationService;
    private final AttractionService attractionService;

    public StationController(StationService stationService, AttractionService attractionService) {
        this.stationService = stationService;

        this.attractionService = attractionService;
    }



    @Override
    public StationResponse getStationInfo(String stationName, String branch) {
        return stationService.getStationByNameAndBranch(stationName,branch);
    }

    @Override
    public PagedResponse<AttractionResponse> getStationAttractions(Long id, int page, int size) {
        return attractionService.getStationAttractions(id,page,size);

    }


    @Override
    public AttractionInfoResponse getAttraction(Long id) {
        return attractionService.findAttractionById(id);
    }
}
