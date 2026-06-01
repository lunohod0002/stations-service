package com.example.backend_vkr.data;

import com.example.backend_vkr.domain.StationAttractions;
import com.example.backend_vkr.domain.repositories.StationAttractionsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPAStationAttractionsRepository extends JpaRepository<StationAttractions,Long>, StationAttractionsRepository {
    @Query(value = "SELECT sa FROM StationAttractions sa " +
            "JOIN FETCH sa.attraction " +
            "WHERE sa.station.id = :stationId",
            countQuery = "SELECT COUNT(sa) FROM StationAttractions sa " +
                    "WHERE sa.station.id = :stationId")
    Page<StationAttractions> findAllStationAttractionsPage(@Param("stationId") Long stationId, Pageable pageable);

    @Query("SELECT sa FROM StationAttractions sa " +
            "JOIN FETCH sa.attraction " +
            "WHERE sa.station.id = :stationId")
    List<StationAttractions> findStationAttractions(@Param("stationId") Long stationId, Pageable pageable);
}



