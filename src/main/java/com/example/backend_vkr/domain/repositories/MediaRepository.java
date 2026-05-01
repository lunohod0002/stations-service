package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.enums.MediaType;

import java.util.List;


public interface MediaRepository {
      List<String> findAllStationMediasByType(MediaType mediaType, Long stationId);
      List<String> findAllAttractionMediasByType(MediaType mediaType, Long attractionId);
}
