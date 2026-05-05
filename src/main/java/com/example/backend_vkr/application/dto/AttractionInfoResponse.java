package com.example.backend_vkr.application.dto;

import java.util.List;

public record AttractionInfoResponse(Long id, String name,String phoneNumber, String email, String address, String workingHours, String description, Integer price, String urlRef, List<String> images, List<String> videos, List<String> audios) {
}
