package com.example.backend_vkr.controllers;



import com.example.backend_vkr.application.dto.*;
import com.example.backend_vkr.application.services.AttractionService;
import com.example.backend_vkr.application.services.StationService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RestController;

//@EnableCaching
@RestController
public class StationController implements StationAPI {

    private final StationService stationService;
    private final AttractionService attractionService;

    public StationController(StationService stationService, AttractionService attractionService) {
        this.stationService = stationService;

        this.attractionService = attractionService;
    }


   //@Cacheable(value = "station")
    @Override
    public StationResponse getStationInfo(String stationName, String branch) {
        return stationService.getStationByNameAndBranch(stationName,branch);
    }

    @Override
    public StationCreatedResponse addStation(@Valid AddStationRequest request) {
        return stationService.addStation(request);
    }
    @Override
    public void deleteStation(Long id) {
        stationService.deleteStation(id);
    }
    @Override
    public AttractionInfoResponse updateAttraction(Long id, AttractionRequest request) {
        return attractionService.updateAttraction(id,request);
    }

    // @Cacheable(value = "allStations")

    @Override
    public StationsResponse getAllStations() {
        return stationService.getAllStations();

    }

    @Override
    public AttractionsResponse getAllAttractions() {
        return attractionService.getAllAttractions();
    }

    // @Cacheable(value = "stationAttractions")
    @Override
    public PagedResponse<AttractionResponse> getStationAttractions(Long id, int page, int size) {
        return attractionService.getStationAttractions(id,page,size);

    }

 //  @Cacheable(value = "attraction")
    @Override
    public AttractionInfoResponse getAttraction(Long id) {
        return attractionService.findAttractionById(id);
    }

    @Override
    public AttractionCreatedResponse addAttraction(@Valid AttractionRequest request) {
        return attractionService.addAttraction(request);
    }

    @Override
    public void deleteAttraction(Long id) {
        attractionService.deleteAttraction(id);
    }
}
