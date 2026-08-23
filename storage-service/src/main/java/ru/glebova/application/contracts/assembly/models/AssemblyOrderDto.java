package ru.glebova.application.contracts.assembly.models;

import ru.glebova.application.contracts.cars.models.CarDto;
import ru.glebova.domain.storageprocesses.AssemblyOrderState;

import java.util.UUID;

public record AssemblyOrderDto(
        UUID id,
        UUID sourceOrderId,
        String sourceOrderType,
        AssemblyOrderState state,
        UUID warehouseAdminId,
        CarDto carDto,
        String traceId)
{ }
