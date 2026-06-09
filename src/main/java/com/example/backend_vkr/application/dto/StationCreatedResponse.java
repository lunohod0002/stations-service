package com.example.backend_vkr.application.dto;

public record StationCreatedResponse(
        Long id,
        String name,
        String branch
) {}