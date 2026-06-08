package com.example.backend_vkr.controllers;


import com.example.backend_vkr.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "stations and attractions",
        description = "API для работы со станциями и достопримечательностями"
)
@RequestMapping({"/api"})
public interface StationAPI {
    @Operation(
            summary = "Получить станцию по названию и ветке"
    )
    @ApiResponses({@ApiResponse(
            responseCode = "200",
            description = "Станция найдена"
    ), @ApiResponse(
            responseCode = "404",
            description = "Станция не найдена",
            content = {@Content(
                    schema = @Schema(
                            implementation = StatusResponse.class
                    )
            )}
    )})
    @GetMapping({"/stations"})
    StationResponse getStationInfo(@RequestParam("stationName") String stationName, @RequestParam("branch") String branch);

    @Operation(
            summary = "Получить достопримечательности по ID станции с фильтрацией и пагинацией"
    )
    @ApiResponses({@ApiResponse(
            responseCode = "200",
            description = "Станция найдена"
    ), @ApiResponse(
            responseCode = "404",
            description = "Станция не найдена",
            content = {@Content(
                    schema = @Schema(
                            implementation = StatusResponse.class
                    )
            )}
    )})

    @GetMapping({"/stations/all"})
    StationsResponse getAllStations();

    @Operation(
            summary = "Получить все достопримечательности"
    )
    @ApiResponses({@ApiResponse(
            responseCode = "200",
            description = "Станция найдена"
    ), @ApiResponse(
            responseCode = "404",
            description = "Станция не найдена",
            content = {@Content(
                    schema = @Schema(
                            implementation = StatusResponse.class
                    )
            )}
    )})

    @GetMapping({"/stations/{stationId}/attractions"})
    PagedResponse<AttractionResponse> getStationAttractions(@PathVariable("stationId") Long id, @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page, @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size);

    @Operation(
            summary = "Получить достопримечательность по ID"
    )
    @ApiResponses({@ApiResponse(
            responseCode = "200",
            description = "Достопримечательность найдена"
    ), @ApiResponse(
            responseCode = "404",
            description = "Достопримечательность не найдена",
            content = {@Content(
                    schema = @Schema(
                            implementation = StatusResponse.class
                    )
            )}
    )})
    @GetMapping({"/attractions/{attractionId}"})
    AttractionInfoResponse getAttraction(@PathVariable("attractionId") Long id);

    @Operation(
            summary = "Добавить достопримечательность"
    )
    @ApiResponses({@ApiResponse(
            responseCode = "201",
            description = "Достопримечательность создана"
    ), @ApiResponse(
            responseCode = "400",
            description = "Невалидный запрос",
            content = {@Content(
                    schema = @Schema(
                            implementation = StatusResponse.class
                    )
            )}
    )})
    @PostMapping({"/attractions"})
    @ResponseStatus(HttpStatus.CREATED)
    AttractionCreatedResponse addAttraction(@RequestBody @Valid AttractionRequest request);
}
