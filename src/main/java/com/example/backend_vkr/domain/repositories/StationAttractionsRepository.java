package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.StationAttractions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StationAttractionsRepository {
    Page<StationAttractions> findAllStationAttractions(Long stationId, Pageable pageable);
    List<StationAttractions> findStationAttractions( Long stationId, Pageable pageable);
}
