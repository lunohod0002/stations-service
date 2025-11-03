package com.example.backend_vkr.repositories;

import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction,Long> {
    @Query("SELECT sa.attraction " +
            "FROM StationAttractions sa WHERE sa.station.id =: id")
    List<Attraction> findAllStationAttractions(@Param("stationId") Long id);
}
