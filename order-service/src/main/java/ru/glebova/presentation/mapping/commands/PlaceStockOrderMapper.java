package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.orders.operations.PlaceStockOrderCommand;
import ru.glebova.presentation.requests.PlaceStockOrderRequest;

@Mapper(componentModel = "spring")
public interface PlaceStockOrderMapper {
    PlaceStockOrderCommand toCommand(PlaceStockOrderRequest request);
}
