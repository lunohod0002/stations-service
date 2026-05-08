package com.example.backend_vkr.application.dto;

public record StationAttractionRequest(
        String stationName,
        String branch,
        int distance
) {}

