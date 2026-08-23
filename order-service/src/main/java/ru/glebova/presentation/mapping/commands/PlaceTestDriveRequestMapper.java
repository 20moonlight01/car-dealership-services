package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.testdriverequests.operations.PlaceTestDriveRequestCommand;
import ru.glebova.presentation.requests.PlaceTestDriveRequestRequest;

@Mapper(componentModel = "spring")
public interface PlaceTestDriveRequestMapper {
    PlaceTestDriveRequestCommand toCommand(PlaceTestDriveRequestRequest request);
}
