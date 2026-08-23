package ru.glebova.application.contracts.orders.operations;

import java.util.UUID;

public record PlaceStockOrderCommand(UUID carId) { }
