package com.example.backend_vkr.application.dto;

import com.example.backend_vkr.domain.enums.MediaType;
import java.util.List;
import java.util.Map;

public record StationNameAndBranch(
        Long id,
        String name,
        String branch,
        Double latitude,
        Double   longitude,
        List<CellTowerRequest> cellTowers // Добавлено

) {
}