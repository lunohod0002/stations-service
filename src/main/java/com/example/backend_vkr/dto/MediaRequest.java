package com.example.backend_vkr.dto;

import com.example.backend_vkr.entities.enums.MediaType;

public record MediaRequest(
        MediaType type,
        String name,
        String urlRef,
        String source

) {
}
