package ru.glebova.application.contracts.testdriverequests.operations;

import java.time.LocalDateTime;
import java.util.UUID;

public record PlaceTestDriveRequestCommand(UUID carId, LocalDateTime time) { }
