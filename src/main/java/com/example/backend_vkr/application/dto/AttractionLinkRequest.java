package com.example.backend_vkr.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AttractionLinkRequest(
        @NotNull(message = "id достопримечательности обязателен") Long attractionId,
        @PositiveOrZero(message = "Расстояние не может быть отрицательным") int distance
) {
}