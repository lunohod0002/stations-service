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

   // @Cacheable(value = "allStations")

    @Override
    public StationsResponse getAllStations() {
        return stationService.getAllStations();

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
