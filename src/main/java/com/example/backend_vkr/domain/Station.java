package com.example.backend_vkr.domain;


import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stations",uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "branch" } ) })
public class Station extends BaseEntity {
    private String name;
    private String branch;
    private List<ExtraService> extraServices;
    private String address;
    private String description;
    private Set<Media> medias;
    private Set<StationAttractions> attractions;
    public Station(String address,List<ExtraService> extraServices, String description, String branch, String name) {
        this.address = address;
        this.description = description;
        this.extraServices=extraServices;
        this.branch = branch;
        this.name = name;
    }
    protected Station() {
    }
    @ManyToMany(cascade =   CascadeType.ALL)
    @JoinTable(name = "station_medias",
            joinColumns = @JoinColumn(name = "station_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "media_id",
                    referencedColumnName = "id"))
    public Set<Media> getMedias() {
        return medias;
    }
    public void setMedias(Set<Media> medias) {
        this.medias = medias;
    }
    @OneToMany(mappedBy = "station",targetEntity = StationAttractions.class,
            fetch = FetchType.LAZY, cascade = CascadeType.ALL)

    public Set<StationAttractions> getAttractions() {
        return attractions;
    }

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ExtraService> getExtraServices() {
        return extraServices;
    }
    public void setExtraServices(List<ExtraService> extraServices) {
        this.extraServices = extraServices;
    }

    public void setAttractions(Set<StationAttractions> attractions) {
        this.attractions = attractions;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "branch", nullable = false)

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Column(name = "description", nullable = false,columnDefinition = "TEXT")

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
}