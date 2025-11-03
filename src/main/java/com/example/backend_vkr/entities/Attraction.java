package com.example.backend_vkr.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "attractions")
public class Attraction extends BaseEntity {
    private String name;
    private String address;
    private String description;
    private String workingHours;
    private int price;
    private String urlRef;
    private Set<Media> medias;
    private Set<StationAttractions> stationAttractions;

    protected Attraction() {
    }

    public Attraction(String name, String address, String description, String workingHours, int price, String urlRef) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.workingHours = workingHours;
        this.price = price;
        this.urlRef = urlRef;
    }

    @ManyToMany
    @JoinTable(name = "attraction_medias",
            joinColumns = @JoinColumn(name = "attraction_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "media_id",
                    referencedColumnName = "id"))
    public Set<Media> getMedias() {
        return medias;
    }

    @OneToMany(mappedBy = "attraction", targetEntity = StationAttractions.class,
            fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<StationAttractions> getStationAttractions() {
        return stationAttractions;
    }

    public void setStationAttractions(Set<StationAttractions> stationAttractions) {
        this.stationAttractions = stationAttractions;
    }

    public void setMedias(Set<Media> medias) {
        this.medias = medias;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", nullable = false)

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "address", nullable = false)

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "working_hours", nullable = false)

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    @Column(name = "price")

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Column(name = "url_ref", nullable = false)

    public String getUrlRef() {
        return urlRef;
    }

    public void setUrlRef(String urlRef) {
        this.urlRef = urlRef;
    }
}