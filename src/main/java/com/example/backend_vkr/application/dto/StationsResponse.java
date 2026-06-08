package com.example.backend_vkr.application.dto;

import java.util.List;

public record StationsResponse(
        List<StationNameAndBranch> stations
) {
}
