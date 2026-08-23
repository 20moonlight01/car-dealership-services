package ru.glebova.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddConfiguredCarRequest(
        @NotNull
        UUID modelId,

        @NotNull
        UUID[] newPartIds,

        @NotBlank
        String color)
{ }
