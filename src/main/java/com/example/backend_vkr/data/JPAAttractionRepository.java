package com.example.backend_vkr.data;

import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.StationAttractions;
import com.example.backend_vkr.domain.repositories.AttractionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAAttractionRepository extends JpaRepository<Attraction,Long>, AttractionRepository {
}