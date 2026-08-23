package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.cars.UserCarDto;
import ru.glebova.application.services.CarStorageClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
@Tag(name = "CarsInfo", description = "Information about cars")
public class CarInfoController {
    private final CarStorageClient carStorageClient;

    public CarInfoController(CarStorageClient carStorageClient) {
        this.carStorageClient = carStorageClient;
    }

    @GetMapping
    @Operation(summary = "Get information about all cars in stock")
    @PreAuthorize("isAuthenticated()")
    public List<UserCarDto> getCarInStockList() {
        var response = carStorageClient.getCarsInStock().getCarsList();
        var cars = new ArrayList<UserCarDto>();
        for (var car : response) {
            cars.add(new UserCarDto(
                    UUID.fromString(car.getId()),
                    UUID.fromString(car.getModelId()),
                    car.getColor(),
                    car.getCarType(),
                    (float) car.getPrice(),
                    car.getPartIdsList().stream()
                            .map(UUID::fromString)
                            .toArray(UUID[]::new)));
        }

        return cars;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get information about particular car in stock")
    @PreAuthorize("isAuthenticated()")
    public UserCarDto getCarInfo(@PathVariable UUID id) {
        var response = carStorageClient.getCarInStock(id.toString());
        return new UserCarDto(
                UUID.fromString(response.getId()),
                UUID.fromString(response.getModelId()),
                response.getColor(),
                response.getCarType(),
                (float) response.getPrice(),
                response.getPartIdsList().stream()
                        .map(UUID::fromString)
                        .toArray(UUID[]::new));
    }
}
