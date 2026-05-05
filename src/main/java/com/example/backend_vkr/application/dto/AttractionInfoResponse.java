package com.example.backend_vkr.application.dto;

import java.util.List;

public record AttractionInfoResponse(Long id, String name, String address, String workingHours, String description, int price, String urlRef, List<String> images, List<String> videos, List<String> audios) {
}
