package com.example.backend_vkr.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StationAttractionLinkRequest(
        @NotNull(message = "id достопримечательности обязателен")
        Long attractionId,

        @NotNull(message = "Расстояние обязательно")
        @PositiveOrZero(message = "Расстояние не может быть отрицательным")
        Integer distance
) {
}