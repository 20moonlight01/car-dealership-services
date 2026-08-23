package ru.glebova.application.contracts.cars.models;

import ru.glebova.enums.CarBody;

import java.util.UUID;

public record CarModelDto(
        UUID id,
        String name,
        String brandName,
        float standardPrice,
        CarBody body,
        EngineDto engineDto,
        CarPartsConfigurationDto baseConfigurationDto)
{ }
