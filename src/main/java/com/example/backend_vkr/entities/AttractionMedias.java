package com.example.backend_vkr.entities;


import jakarta.persistence.*;

import java.io.Serializable;

public class AttractionMedias implements Serializable {

    private Attraction attraction;
    private Media media;

    public AttractionMedias(Attraction attraction, Media media) {
        this.attraction = attraction;
        this.media = media;
    }

    protected AttractionMedias() {
    }
    @ManyToOne
    @JoinColumn(name = "attraction_id")
    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
    @ManyToOne
    @JoinColumn(name = "media_id")
    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
}