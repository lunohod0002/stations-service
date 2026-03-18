package com.example.backend_vkr.dto;

public record AttractionRequest(String description, String address, int price, String workingHours, String phoneNumber, String email, String urlRef, String name) {
}
