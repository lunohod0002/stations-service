package com.example.backend_vkr.business;


import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "extra_services")
public class ExtraService extends BaseEntity {

    private String name;
    private Station station;

    protected ExtraService() {
    }

    public ExtraService(String name, Station station) {
        this.name = name;
        this.station = station;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Автоматическое удаление услуги при удалении станции
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
