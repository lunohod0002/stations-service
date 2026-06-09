package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.Station;
import com.example.backend_vkr.domain.StationAttractions;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends BaseRepository<Station,Long> {

    Optional<Station> findStationById( Long stationId);
    List<Station> findAllWithMedias();

    Optional<Station> findByNameAndBranch(String name,String branch);

}
