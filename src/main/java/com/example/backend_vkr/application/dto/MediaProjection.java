package com.example.backend_vkr.application.dto;

import com.example.backend_vkr.domain.enums.MediaType;

public record MediaProjection(MediaType type, String urlRef) {}

