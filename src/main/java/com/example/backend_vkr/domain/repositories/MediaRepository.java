package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.application.dto.AttractionPhoto;
import com.example.backend_vkr.application.dto.MediaProjection;
import com.example.backend_vkr.domain.enums.MediaType;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;


public interface MediaRepository {
      List<String> findAllStationMediasByType(MediaType mediaType, Long stationId);
      List<String> findAllAttractionMediasByType(MediaType mediaType, Long attractionId);
      List<AttractionPhoto> findPhotosByAttractionIds(Collection<Long> attractionIds,
                                                      MediaType mediaType);
      List<MediaProjection> findAllStationMedias(Long stationId);
}
