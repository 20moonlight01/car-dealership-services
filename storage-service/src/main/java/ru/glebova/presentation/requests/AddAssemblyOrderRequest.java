package ru.glebova.presentation.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddAssemblyOrderRequest(
        @NotNull
        UUID sourceOrderId,

        @NotBlank
        String sourceOrderType,

        @NotNull
        UUID carId,

        @NotBlank
        String traceId)
{ }
