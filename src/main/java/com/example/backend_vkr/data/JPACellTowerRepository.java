package com.example.backend_vkr.data;

import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.CellTower;
import com.example.backend_vkr.domain.repositories.AttractionRepository;
import com.example.backend_vkr.domain.repositories.CellTowerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPACellTowerRepository extends JpaRepository<CellTower, Long>, CellTowerRepository {

}
