package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.StationAttractions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface StationAttractionsRepository extends BaseRepository<StationAttractions,Long>{
    Page<StationAttractions> findAllStationAttractionsByStationPage(Long stationId, Pageable pageable);
    List<StationAttractions> findStationAttractionsByStation( Long stationId, Pageable pageable);
    List<StationAttractions> findByStationId(Long stationId);
    void deleteByStationIdAndAttractionIdIn(Long stationId,
                                          Collection<Long> attractionIds);


    List<StationAttractions> findStationAttractionsByAttraction(Long attractionId);
}
