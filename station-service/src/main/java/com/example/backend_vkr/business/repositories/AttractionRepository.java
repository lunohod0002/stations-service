package com.example.backend_vkr.business.repositories;

import com.example.backend_vkr.business.Attraction;
import com.example.backend_vkr.business.StationAttractions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface AttractionRepository  {

    Page<StationAttractions> findAllStationAttractions(Long stationId, Pageable pageable);
}
