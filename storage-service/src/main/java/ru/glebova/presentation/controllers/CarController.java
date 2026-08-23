package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.cars.models.CarDto;
import ru.glebova.application.services.CarService;
import ru.glebova.dto.CarInfoDto;
import ru.glebova.presentation.mapping.commands.AddCarToStockMapper;
import ru.glebova.presentation.mapping.commands.AddConfiguredCarMapper;
import ru.glebova.presentation.mapping.commands.GetFilteredCarsMapper;
import ru.glebova.presentation.mapping.dto.CarMapper;
import ru.glebova.presentation.requests.AddCarToStockRequest;
import ru.glebova.presentation.requests.GetFilteredCarsRequest;
import ru.glebova.requests.AddConfiguredCarRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Cars", description = "Operations with cars")
public class CarController {
    private final CarService carService;
    private final GetFilteredCarsMapper getFilteredCarsMapper;
    private final AddCarToStockMapper addCarToStockMapper;
    private final AddConfiguredCarMapper addConfiguredCarMapper;
    private final CarMapper carMapper;

    public CarController(
            CarService carService,
            GetFilteredCarsMapper getFilteredCarsMapper,
            AddCarToStockMapper addCarToStockMapper,
            AddConfiguredCarMapper addConfiguredCarMapper,
            CarMapper carMapper)
    {
        this.carService = carService;
        this.getFilteredCarsMapper = getFilteredCarsMapper;
        this.addCarToStockMapper = addCarToStockMapper;
        this.addConfiguredCarMapper = addConfiguredCarMapper;
        this.carMapper = carMapper;
    }

    @GetMapping("/{id}/in-stock")
    public CarInfoDto isInStock(@PathVariable UUID id) {
        return carService.isInStock(id);
    }

    @PostMapping("/all/search")
    @Operation(summary = "Get information about all cars in stock")
    @PreAuthorize("isAuthenticated()")
    public List<CarDto> getCarInStockList(@Valid @RequestBody GetFilteredCarsRequest request) {
        return carService.getCarInStockList(getFilteredCarsMapper.toCommand(request))
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get information about particular car in stock")
    @PreAuthorize("isAuthenticated()")
    public CarDto getCarInfo(@PathVariable UUID id) {
        return carMapper.toDto(
                carService.getCarInfo(id));
    }

    @PostMapping
    @Operation(summary = "Add information about particular car in stock")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public CarDto addCarToStock(@Valid @RequestBody AddCarToStockRequest request) {
        return carMapper.toDto(
                carService.addCarToStock(addCarToStockMapper.toCommand(request)));
    }

    @PostMapping("/configured")
    @Operation(summary = "Add information about particular configured car")
    public CarInfoDto addConfiguredCar(@Valid @RequestBody AddConfiguredCarRequest request) {
        var car = carService.addConfiguredCar(addConfiguredCarMapper.toCommand(request));

        return new CarInfoDto(
                car.getId(), car.getPrice().value());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Erase information about particular car in stock")
    @PreAuthorize("hasRole('ADMIN')")
    public CarDto removeCarFromStock(@PathVariable UUID id) {
        return carMapper.toDto(
                carService.removeCarFromStock(id));
    }

    @GetMapping("/{id}/is-testable")
    public boolean isTestable(@PathVariable UUID id) {
        return carService.isTestable(id);
    }

    @PostMapping("/testables/all/search")
    @Operation(summary = "Get information about all testable cars")
    @PreAuthorize("isAuthenticated()")
    public List<CarDto> getTestableCarList(@Valid @RequestBody GetFilteredCarsRequest request) {
        return carService.getTestableCarList(getFilteredCarsMapper.toCommand(request))
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @PostMapping("/testables/{id}")
    @Operation(summary = "Mark particular car in stock testable")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CarDto addTestableCar(@PathVariable UUID id) {
        return carMapper.toDto(
                carService.addTestableCar(id));
    }

    @DeleteMapping("/testables/{id}")
    @Operation(summary = "Mark particular testable car not testable")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CarDto removeTestableCar(@PathVariable UUID id) {
        return carMapper.toDto(
                carService.removeTestableCar(id));
    }
}