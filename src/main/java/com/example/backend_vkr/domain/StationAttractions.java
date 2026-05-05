package com.example.backend_vkr.domain;


import jakarta.persistence.*;

@IdClass(StationAttractionKey.class)

@Entity
public class StationAttractions {
    private Station station;
    private Attraction attraction;
    private int distance;

    public StationAttractions( Station station, Attraction attraction, int distance) {
        this.station = station;
        this.attraction = attraction;
        this.distance = distance;
    }



    protected StationAttractions() {
    }


    @Column(name = "distance")
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    @ManyToOne
    @Id
    @JoinColumn(name = "station_id")
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
    @ManyToOne
    @Id

    @JoinColumn(name = "attraction_id")
    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
}