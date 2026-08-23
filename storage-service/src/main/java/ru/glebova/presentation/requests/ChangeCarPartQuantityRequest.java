package ru.glebova.presentation.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChangeCarPartQuantityRequest(
        @NotNull
        @Positive
        int quantity)
{ }
