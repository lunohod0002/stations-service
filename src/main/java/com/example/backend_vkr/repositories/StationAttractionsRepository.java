package com.example.backend_vkr.repositories;

import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.StationAttractions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationAttractionsRepository extends JpaRepository<StationAttractions,Long> {

}
