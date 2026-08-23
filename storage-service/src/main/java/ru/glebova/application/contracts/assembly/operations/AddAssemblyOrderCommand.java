package ru.glebova.application.contracts.assembly.operations;

import java.util.UUID;

public record AddAssemblyOrderCommand(
        UUID sourceOrderId,
        String sourceOrderType,
        UUID carId,
        String traceId)
{ }
