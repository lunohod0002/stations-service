package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.CellTower;
import com.example.backend_vkr.domain.Station;

import java.util.List;
import java.util.Optional;

public interface CellTowerRepository extends BaseRepository<CellTower,Long> {

    void deleteByStationId(Long stationId);
    List<CellTower> findAllByStationId(Long stationId);

}
