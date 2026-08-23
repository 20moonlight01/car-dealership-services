package ru.glebova.application.contracts.cars.operations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddConfiguredCarCommand(
        @NotNull
        UUID modelId,

        @NotNull
        UUID[] newPartIds,

        @NotBlank
        String color)
{ }
