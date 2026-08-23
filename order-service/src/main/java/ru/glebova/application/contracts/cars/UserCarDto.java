package ru.glebova.application.contracts.cars;

import java.util.UUID;

public record UserCarDto(
        UUID id,
        UUID modelId,
        String color,
        String carType,
        float price,
        UUID[] partIds)
{ }
