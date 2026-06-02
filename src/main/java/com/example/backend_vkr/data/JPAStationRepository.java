package com.example.backend_vkr.data;

import com.example.backend_vkr.domain.Station;
import com.example.backend_vkr.domain.repositories.StationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JPAStationRepository extends JpaRepository<Station,Long>, StationRepository {
    @Query("SELECT st FROM Station st where st.id = :stationId")
    Optional<Station> findStationById(@Param(value = "stationId") Long stationId);

    @Query("SELECT DISTINCT s FROM Station s " + "LEFT JOIN FETCH s.extraServices " + "WHERE s.name = :name AND s.branch = :branch")
    Optional<Station> findByNameAndBranch(@Param("name") String name, @Param("branch") String branch);


}
