package com.example.backend_vkr.business.repositories;

import com.example.backend_vkr.business.Media;
import com.example.backend_vkr.business.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MediaRepository {
      List<String> findAllStationMediasByType(MediaType mediaType, Long stationId);
      List<String> findAllAttractionMediasByType(MediaType mediaType, Long attractionId);
}
