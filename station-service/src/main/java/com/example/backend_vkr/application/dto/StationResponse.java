package com.example.backend_vkr.application.dto;

import java.util.List;

public record StationResponse(Long id, String name, String branch, String address, String description, List<String> imagesRef, List<String> videosRef, List<String> audiosRef, List<String> extraServices, List<AttractionResponse> attractionResponseList) {
}
