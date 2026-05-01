package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.Station;

import java.util.Optional;

public interface StationRepository  {

    Optional<Station> findStationById( Long stationId);

    Optional<Station> findByNameAndBranch(String name,String branch);

}
