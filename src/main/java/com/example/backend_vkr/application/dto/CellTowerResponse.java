package com.example.backend_vkr.application.dto;

public record CellTowerResponse(
        Long id,
        String cid,
        String lac,
        String mcc,
        String mnc,
        String radio
) {}