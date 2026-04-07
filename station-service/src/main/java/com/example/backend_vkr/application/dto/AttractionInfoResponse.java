package com.example.backend_vkr.application.dto;

import java.util.List;

public record AttractionInfoResponse(Long id, String name, String address, String working_hours, String info, int price, String urlRef, List<String> imagesRef, List<String> videosRef, List<String> audiosRef) {
}
