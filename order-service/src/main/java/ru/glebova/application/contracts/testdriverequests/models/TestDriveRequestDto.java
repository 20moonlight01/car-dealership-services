package ru.glebova.application.contracts.testdriverequests.models;

import ru.glebova.domain.testdriverequests.TestDriveRequestState;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestDriveRequestDto(
        UUID id,
        UUID clientId,
        UUID carId,
        LocalDateTime time,
        TestDriveRequestState state)
{ }
