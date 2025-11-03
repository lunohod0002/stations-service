package com.example.backend_vkr.repositories;

import com.example.backend_vkr.entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station,Long> {
    @Query("SELECT st FROM Station st where st.name=:name and st.branch=:branch")
    Optional<Station> findByNameAndBranch(@Param("name") String name,@Param("branch")String branch);

}
