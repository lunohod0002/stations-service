package com.example.backend_vkr.repositories;

import com.example.backend_vkr.entities.Media;
import com.example.backend_vkr.entities.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {
    @Query("SELECT distinct media.urlRef " +
            "FROM Media media JOIN media.stations station where station.id =: stationId")
    List<String> findAllStationMediasByTpe(MediaType mediaType, Long stationId);
}
//select distinct distributor
//from Distributor distributor
//join distributor.towns town
//join town.district district
//where district.name = :name