package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.cars.models.CarModelDto;
import ru.glebova.application.contracts.cars.models.CarPartDto;
import ru.glebova.application.contracts.cars.operations.AddCarPartCommand;
import ru.glebova.application.services.CarDetailsService;
import ru.glebova.presentation.mapping.commands.AddCarModelMapper;
import ru.glebova.presentation.mapping.commands.AddCarPartMapper;
import ru.glebova.presentation.mapping.commands.GetFilteredCarModelsMapper;
import ru.glebova.presentation.mapping.dto.CarModelMapper;
import ru.glebova.presentation.mapping.dto.CarPartMapper;
import ru.glebova.presentation.requests.AddCarModelRequest;
import ru.glebova.presentation.requests.AddCarPartRequest;
import ru.glebova.presentation.requests.GetFilteredCarModelsRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/car-details")
@Tag(name = "Car Details", description = "Operations with car models and car parts")
public class CarDetailsController {
    private final CarDetailsService carDetailsService;
    private final AddCarModelMapper addCarModelMapper;
    private final AddCarPartMapper addCarPartMapper;
    private final GetFilteredCarModelsMapper getFilteredCarModelsMapper;
    private final CarModelMapper carModelMapper;
    private final CarPartMapper carPartMapper;

    public CarDetailsController(
            CarDetailsService carDetailsService,
            AddCarModelMapper addCarModelMapper,
            AddCarPartMapper addCarPartMapper,
            GetFilteredCarModelsMapper getFilteredCarModelsMapper,
            CarModelMapper carModelMapper,
            CarPartMapper carPartMapper)
    {
        this.carDetailsService = carDetailsService;
        this.addCarModelMapper = addCarModelMapper;
        this.addCarPartMapper = addCarPartMapper;
        this.getFilteredCarModelsMapper = getFilteredCarModelsMapper;
        this.carModelMapper = carModelMapper;
        this.carPartMapper = carPartMapper;
    }

    @PostMapping("/models/all/search")
    @Operation(summary = "Get information about all car models")
    @PreAuthorize("isAuthenticated()")
    public List<CarModelDto> getCarModelList(@Valid @RequestBody GetFilteredCarModelsRequest request) {
        return carDetailsService.getCarModelList(getFilteredCarModelsMapper.toCommand(request))
                .stream()
                .map(carModelMapper::toDto)
                .toList();
    }

    @GetMapping("/models/{id}")
    @Operation(summary = "Get information about particular car model")
    @PreAuthorize("isAuthenticated()")
    public CarModelDto getCarModelInfo(@PathVariable UUID id) {
        return carModelMapper.toDto(
                carDetailsService.getCarModelInfo(id));
    }

    @PostMapping("/models")
    @Operation(summary = "Add information about particular car model")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public CarModelDto addCarModel(@Valid @RequestBody AddCarModelRequest request) {
        return carModelMapper.toDto(
                carDetailsService.addCarModel(addCarModelMapper.toCommand(request)));
    }

    @DeleteMapping("/models/{id}")
    @Operation(summary = "Erase information about particular car model")
    @PreAuthorize("hasRole('ADMIN')")
    public CarModelDto removeCarModel(@PathVariable UUID id) {
        return carModelMapper.toDto(
                carDetailsService.removeCarModel(id));
    }

    @GetMapping("/parts/all")
    @Operation(summary = "Get information about all car parts")
    @PreAuthorize("isAuthenticated()")
    public List<CarPartDto> getCarPartList() {
        return carDetailsService.getCarPartList()
                .stream()
                .map(carPartMapper::toDto)
                .toList();
    }

    @GetMapping("/parts/{id}")
    @Operation(summary = "Get information about particular car part")
    @PreAuthorize("isAuthenticated()")
    public CarPartDto getCarPartInfo(@PathVariable UUID id) {
        return carPartMapper.toDto(
                carDetailsService.getCarPartInfo(id));
    }

    @PostMapping("/parts")
    @Operation(summary = "Add information about particular car part")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public CarPartDto addCarPart(@Valid @RequestBody AddCarPartRequest request) {
        return switch (request) {
            case AddCarPartRequest.AddSteeringWheelRequest _ ->
                    carPartMapper.toDto(
                        carDetailsService.AddSteeringWheel(
                            (AddCarPartCommand.AddSteeringWheelCommand) addCarPartMapper.toCommand(request)));
            case AddCarPartRequest.AddWheelsRequest _ ->
                    carPartMapper.toDto(
                        carDetailsService.AddWheels(
                            (AddCarPartCommand.AddWheelsCommand) addCarPartMapper.toCommand(request)));
            case AddCarPartRequest.AddInteriorRequest _ ->
                    carPartMapper.toDto(
                        carDetailsService.AddInterior(
                            (AddCarPartCommand.AddInteriorCommand) addCarPartMapper.toCommand(request)));
            case AddCarPartRequest.AddTransmissionRequest _ ->
                    carPartMapper.toDto(
                        carDetailsService.AddTransmission(
                            (AddCarPartCommand.AddTransmissionCommand) addCarPartMapper.toCommand(request)));
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }

    @DeleteMapping("/parts/{id}")
    @Operation(summary = "Erase information about particular car part")
    @PreAuthorize("hasRole('ADMIN')")
    public CarPartDto removeCarPart(@PathVariable UUID id) {
        return carPartMapper.toDto(
                carDetailsService.removeCarPart(id));
    }
}
