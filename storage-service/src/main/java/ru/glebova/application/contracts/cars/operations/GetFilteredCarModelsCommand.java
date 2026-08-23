package ru.glebova.application.contracts.cars.operations;

import java.util.UUID;

public record GetFilteredCarModelsCommand(
        String brandName,
        UUID steeringWheelId,
        UUID wheelsId,
        UUID interiorId,
        UUID transmissionId)
{ }
