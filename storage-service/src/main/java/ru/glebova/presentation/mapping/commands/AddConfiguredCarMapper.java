package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.cars.operations.AddConfiguredCarCommand;
import ru.glebova.requests.AddConfiguredCarRequest;

@Mapper(componentModel = "spring")
public interface AddConfiguredCarMapper {
    AddConfiguredCarCommand toCommand(AddConfiguredCarRequest request);
}
