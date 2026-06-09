package com.example.backend_vkr.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CellTowerRequest(
        @NotBlank String cid,
        @NotBlank String lac,
        @NotBlank String mcc,
        @NotBlank String mnc,
        @NotBlank String radio
) {}