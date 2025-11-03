package com.example.backend_vkr.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "stations",uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "branch" } ) })
public class Station extends BaseEntity {
    private String name;
    private int branch;

    private String address;
    private String description;

    public Station(String name, String address, String description,int branch) {
        this.name = name;
        this.address = address;
        this.branch=branch;
        this.description = description;
    }

    protected Station() {
    }


    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "branch", nullable = false)

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
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
}