package com.example.backend_vkr.application.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddStationRequest(
        @NotBlank(message = "Название станции обязательно")
        String name,

        @NotBlank(message = "Ветка обязательна")
        String branch,

        @NotBlank(message = "Описание обязательно")
        String description,

        @Valid
        StationMediasRequest media,

        @Valid
        List<CellTowerRequest> cellTowers
) {}