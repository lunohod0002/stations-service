package com.example.backend_vkr.repositories;

import com.example.backend_vkr.entities.Media;
import com.example.backend_vkr.entities.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {
    @Query("SELECT media.urlRef " +
            "FROM Media media JOIN media.stations station where station.id = :stationId and media.type = :mediaType")
    List<String> findAllStationMediasByType(@Param("mediaType") MediaType mediaType,@Param("stationId") Long stationId);
    @Query("SELECT media.urlRef " +
            "FROM Media media JOIN media.attractions attraction where attraction.id = :attractionId and media.type = :mediaType")
    List<String> findAllAttractionMediasByType(@Param("mediaType") MediaType mediaType,@Param("attractionId") Long attractionId);
}
