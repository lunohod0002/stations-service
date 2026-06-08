package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.StationAttractions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StationAttractionsRepository extends BaseRepository<StationAttractions,Long>{
    Page<StationAttractions> findAllStationAttractionsByStationPage(Long stationId, Pageable pageable);
    List<StationAttractions> findStationAttractionsByStation( Long stationId, Pageable pageable);

    List<StationAttractions> findStationAttractionsByAttraction(Long attractionId);
}
