package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.cars.operations.GetFilteredCarModelsCommand;
import ru.glebova.presentation.requests.GetFilteredCarModelsRequest;

@Mapper(componentModel = "spring")
public interface GetFilteredCarModelsMapper {
    GetFilteredCarModelsCommand toCommand(GetFilteredCarModelsRequest request);
}
