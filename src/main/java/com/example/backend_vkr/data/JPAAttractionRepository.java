package com.example.backend_vkr.data;

import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.StationAttractions;
import com.example.backend_vkr.domain.repositories.AttractionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAAttractionRepository extends JpaRepository<Attraction,Long>, AttractionRepository {
    @Query(value = "SELECT sa FROM StationAttractions sa " +
            "JOIN FETCH sa.attraction " +
            "WHERE sa.station.id = :stationId",
            countQuery = "SELECT COUNT(sa) FROM StationAttractions sa " +
                    "WHERE sa.station.id = :stationId")
    Page<StationAttractions> findAllStationAttractions(@Param("stationId") Long stationId, Pageable pageable);}
