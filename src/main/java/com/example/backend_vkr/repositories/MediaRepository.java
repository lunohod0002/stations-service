package com.example.backend_vkr.repositories;

import com.example.backend_vkr.entities.Media;
import com.example.backend_vkr.entities.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {
    @Query("SELECT m.urlRef " +
            "FROM StationMedias sm JOIN sm.station st JOIN sm.media m where st.id =: stationId")
    List<String> findAllStationMediasByTpe(MediaType mediaType, Long stationId);
}
