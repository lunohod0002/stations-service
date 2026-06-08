package com.example.backend_vkr.data;

import com.example.backend_vkr.application.dto.AttractionPhoto;
import com.example.backend_vkr.application.dto.MediaProjection;
import com.example.backend_vkr.domain.Media;
import com.example.backend_vkr.domain.enums.MediaType;
import com.example.backend_vkr.domain.repositories.MediaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface JPAMediaRepository extends JpaRepository<Media,Long>, MediaRepository {
    @Query("SELECT media.urlRef " +
            "FROM Media media JOIN media.stations station where station.id = :stationId and media.type = :mediaType")
    List<String> findAllStationMediasByType(@Param("mediaType") MediaType mediaType,@Param("stationId") Long stationId);
    @Query("SELECT media.urlRef " +
            "FROM Media media JOIN media.attractions attraction where attraction.id = :attractionId and media.type = :mediaType ")
    List<String> findAllAttractionMediasByType(@Param("mediaType") MediaType mediaType,@Param("attractionId") Long attractionId);
    @Query("SELECT new com.example.backend_vkr.application.dto.AttractionPhoto(attraction.id, media.urlRef) " +
            "FROM Media media JOIN media.attractions attraction " +
            "WHERE attraction.id IN :attractionIds AND media.type = :mediaType")
    List<AttractionPhoto> findPhotosByAttractionIds(@Param("attractionIds") Collection<Long> attractionIds,
                                                    @Param("mediaType") MediaType mediaType);

    @Query("SELECT new com.example.backend_vkr.application.dto.MediaProjection(media.type, media.urlRef) " +
            "FROM Media media JOIN media.stations station " +
            "WHERE station.id = :stationId")
    List<MediaProjection> findAllStationMedias(@Param("stationId") Long stationId);

    @Modifying
    @Query("DELETE FROM Media m WHERE m.id IN :ids AND m.attractions IS EMPTY")
    void deleteOrphansByIds(@Param("ids") List<Long> ids);


}

