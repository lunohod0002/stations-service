package com.example.backend_vkr.entities;


import com.example.backend_vkr.entities.enums.MediaType;
import jakarta.persistence.*;

@Entity
@Table(name = "medias")
public class Media extends BaseEntity {
    private MediaType type;

    private String name;
    private String urlRef;


    public Media(MediaType type, String urlRef) {
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

    @Enumerated(EnumType.STRING)
    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
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