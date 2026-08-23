package ru.glebova.application.contracts.cars.models;

import java.util.UUID;

public record CarDto(
        UUID id,
        CarModelDto modelDto,
        String color,
        CarPartsConfigurationDto configurationDto,
        float price)
{ }
