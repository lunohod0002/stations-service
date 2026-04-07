package com.example.backend_vkr.business;

import java.io.Serializable;

public class StationAttractionKey implements Serializable {

    private Station station;
    private Attraction attraction;

    protected StationAttractionKey() {
    }

    public StationAttractionKey(Station station, Attraction attraction) {
        this.station = station;
        this.attraction = attraction;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
}