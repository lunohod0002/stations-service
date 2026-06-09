package com.example.backend_vkr.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateStationRequest(
        @NotBlank(message = "Название станции обязательно")
        String name,

        @NotBlank(message = "Ветка обязательна")
        String branch,

        @NotBlank(message = "Описание обязательно")
        String description,

        String address,

        @Valid
        StationMediasRequest media,

        @Valid
        List<CellTowerRequest> cellTowers,

        // Итоговый список привязок: чего нет в списке — будет удалено,
        // что есть и совпадает по id — обновится; новые id будут привязаны.
        @Valid
        List<StationAttractionLinkRequest> attractions
) {
}