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
    @Operation(summary = "Обновить станцию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Станция обновлена"),
            @ApiResponse(responseCode = "404", description = "Станция не найдена",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Невалидный запрос",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PutMapping("/stations/{stationId}")
    StationResponse updateStation(
            @PathVariable("stationId") Long id,
            @RequestBody @Valid UpdateStationRequest request);

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

    @DeleteMapping("/stations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteStation(@PathVariable Long id);


    @Operation(summary = "Обновить достопримечательность")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Достопримечательность обновлена"),
            @ApiResponse(responseCode = "404", description = "Достопримечательность не найдена",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Невалидный запрос",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PutMapping("/attractions/{attractionId}")
    AttractionInfoResponse updateAttraction(
            @PathVariable("attractionId") Long id,
            @RequestBody @Valid AttractionRequest request);
    @Operation(
            summary = "Получить все станции"
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
    @GetMapping({"/attractions"})
    AttractionsResponse getAllAttractions();

    @Operation(
            summary = "Получить все достопримечательности станции"
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


    @DeleteMapping({"/attractions/{attractionId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAttraction(@PathVariable("attractionId") Long id);
}
