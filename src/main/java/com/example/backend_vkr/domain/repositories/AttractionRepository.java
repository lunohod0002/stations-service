package com.example.backend_vkr.domain.repositories;

import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.StationAttractions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttractionRepository extends BaseRepository<Attraction,Long> {
    List<Attraction> findAllWithMedias();

}
