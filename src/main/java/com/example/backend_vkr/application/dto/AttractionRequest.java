package com.example.backend_vkr.application.dto;

import java.util.List;

public record AttractionRequest(String description, String address, Integer price, String workingHours, String phoneNumber, String email, String urlRef, String name, List<MediaRequest> medias, List<StationAttractionRequest> stationAttractions) {
}
