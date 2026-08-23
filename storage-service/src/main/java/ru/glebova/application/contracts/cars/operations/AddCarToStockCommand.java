package ru.glebova.application.contracts.cars.operations;

import java.util.UUID;

public record AddCarToStockCommand(UUID modelId, String color, UUID[] configuration) { }
