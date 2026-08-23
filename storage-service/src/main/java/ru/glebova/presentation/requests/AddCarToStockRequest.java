package ru.glebova.presentation.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCarToStockRequest(
        @NotNull
        UUID modelId,

        @NotBlank
        String color,

        @NotNull
        UUID[] configuration)
{ }
