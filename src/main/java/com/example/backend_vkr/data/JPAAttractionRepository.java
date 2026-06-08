package com.example.backend_vkr.data;

import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.repositories.AttractionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPAAttractionRepository extends JpaRepository<Attraction, Long>, AttractionRepository {

    @Query("SELECT DISTINCT a FROM Attraction a LEFT JOIN FETCH a.medias")
    List<Attraction> findAllWithMedias();

}