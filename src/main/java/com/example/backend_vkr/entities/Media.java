package com.example.backend_vkr.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "medias")
public class Media extends BaseEntity {
    private String type;

    private String name;
    private String urlRef;


    public Media(String type, String urlRef) {
        this.type = type;
        this.urlRef = urlRef;
    }

    protected Media() {
    }


    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "type", nullable = false)

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @Column(name = "url_ref", nullable = false)

    public String getUrlRef() {
        return urlRef;
    }

    public void setUrlRef(String urlRef) {
        this.urlRef = urlRef;
    }
}