package com.example.backend_vkr.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "stations",uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "branch" } ) })
public class Station extends BaseEntity {
    private String name;
    private String branch;
    private String builtAt;
    private Map<String, Object> referenceData;

    private String address;
    private String description;
    private Set<Media> medias;
    private Set<StationAttractions> attractions;

    public Station(String address, String description, Map<String, Object> referenceData, String builtAt, String branch, String name) {
        this.address = address;
        this.description = description;
        this.referenceData = referenceData;
        this.builtAt = builtAt;
        this.branch = branch;
        this.name = name;
    }

    public Station(String name, String address, String description, String branch) {
        this.name = name;
        this.address = address;
        this.branch=branch;
        this.description = description;
    }

    protected Station() {
    }
    @ManyToMany
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
    @Column(name = "reference_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    public Map<String, Object> getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(Map<String, Object> referenceData) {
        this.referenceData = referenceData;
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
    @Column(name = "built_at", nullable = false)

    public String getBuiltAt() {
        return builtAt;
    }

    public void setBuiltAt(String builtAt) {
        this.builtAt = builtAt;
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