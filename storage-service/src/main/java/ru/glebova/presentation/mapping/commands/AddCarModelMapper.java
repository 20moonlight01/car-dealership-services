package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.operations.AddCarModelCommand;
import ru.glebova.presentation.requests.AddCarModelRequest;

@Mapper(componentModel = "spring")
public interface AddCarModelMapper {
    @Mapping(source = "engine.fuel", target = "fuel")
    @Mapping(source = "engine.power", target = "power")
    @Mapping(source = "engine.volume", target = "volume")
    AddCarModelCommand toCommand(AddCarModelRequest request);
}
