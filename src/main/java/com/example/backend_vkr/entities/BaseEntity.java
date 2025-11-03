package com.example.backend_vkr.entities;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }
}