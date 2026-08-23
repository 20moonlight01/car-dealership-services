package ru.glebova.presentation.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PlaceStockOrderRequest(
        @NotNull
        UUID carId)
{ }
