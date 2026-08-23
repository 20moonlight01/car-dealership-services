package ru.glebova.application.contracts.orders.operations;

import java.util.UUID;

public record PlaceConfiguredOrderCommand(
        UUID modelId,
        UUID[] newPartIds,
        String color)
{ }
