package com.example.backend_vkr.data;

import com.example.backend_vkr.business.StationAttractions;
import com.example.backend_vkr.business.repositories.StationAttractionsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAStationAttractionsRepository extends JpaRepository<StationAttractions,Long>, StationAttractionsRepository {

}
