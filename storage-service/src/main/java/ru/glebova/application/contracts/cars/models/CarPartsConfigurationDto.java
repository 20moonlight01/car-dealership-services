package ru.glebova.application.contracts.cars.models;

public record CarPartsConfigurationDto(
        CarPartDto steeringWheelDto,
        CarPartDto wheelsDto,
        CarPartDto interiorDto,
        CarPartDto transmissionDto)
{ }
