package com.example.backend_vkr.application.dto;

import com.example.backend_vkr.domain.enums.MediaType;

public record MediaRequest(
        MediaType type,
        String name,
        String urlRef,
        String source

) {
}
