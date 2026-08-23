package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.cars.operations.AddCarToStockCommand;
import ru.glebova.presentation.requests.AddCarToStockRequest;

@Mapper(componentModel = "spring")
public interface AddCarToStockMapper {
    AddCarToStockCommand toCommand(AddCarToStockRequest request);
}
