package ru.glebova.presentation.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record PlaceTestDriveRequestRequest(
        @NotNull
        UUID carId,

        @NotNull
        @Future
        LocalDateTime time)
{ }
