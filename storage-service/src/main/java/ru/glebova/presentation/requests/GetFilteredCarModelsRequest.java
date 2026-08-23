package ru.glebova.presentation.requests;

import java.util.UUID;

public record GetFilteredCarModelsRequest(
        String brandName,
        UUID steeringWheelId,
        UUID wheelsId,
        UUID interiorId,
        UUID transmissionId)
{ }
