package com.example.backend_vkr.business.repositories;

import com.example.backend_vkr.business.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface StationRepository  {

    Optional<Station> findStationById( Long stationId);

    Optional<Station> findByNameAndBranch(String name,String branch);

}
