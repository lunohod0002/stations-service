package com.example.backend_vkr.entities;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "station_attractions")
public class StationAttractions implements Serializable {

    private Station station;
    private Attraction attraction;
    private String distance;

    public StationAttractions(Station station, Attraction attraction,String distance) {
        this.station = station;
        this.attraction = attraction;
        this.distance=distance;
    }

    protected StationAttractions() {
    }


    @Column(name = "distance")
    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
    @Id
    @ManyToOne
    @JoinColumn(name = "station_id")
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
    @Id
    @ManyToOne
    @JoinColumn(name = "attraction_id")
    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
}