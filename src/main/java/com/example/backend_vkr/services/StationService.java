package com.example.backend_vkr.services;

import com.example.backend_vkr.exception.ResourceNotFoundException;
import com.example.backend_vkr.models.AttractionInfoResponse;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.PagedResponse;
import com.example.backend_vkr.models.StationResponse;
import com.example.backend_vkr.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
@Service
public class StationService {
    private final InMemoryStorage storage;
    public StationService(InMemoryStorage storage) {
        this.storage = storage;

    }

    public StationResponse findStationById(Long id) {
        return Optional.ofNullable(storage.stations.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Station", id));
    }
    public AttractionInfoResponse findAttractionById(Long id) {
        return Optional.ofNullable(storage.attractions.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Attraction", id));
    }



}
