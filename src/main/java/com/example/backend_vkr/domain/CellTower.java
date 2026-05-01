package com.example.backend_vkr.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "cell_towers")

public class CellTower extends BaseEntity {
    private String cid;
    private String lac;

    private String mcc;

    private String mnc;
    private String radio;



    private Station station;
    @ManyToOne
    @JoinColumn(name = "station_id")
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public CellTower(String cid, String lac, String mcc, String mnc, String radio, Station station) {
        this.cid = cid;
        this.lac = lac;
        this.mcc = mcc;
        this.mnc = mnc;
        this.radio = radio;
        this.station = station;
    }

    protected CellTower() {
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }
}