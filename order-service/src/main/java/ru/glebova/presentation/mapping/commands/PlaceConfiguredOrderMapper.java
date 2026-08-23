package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.orders.operations.PlaceConfiguredOrderCommand;
import ru.glebova.presentation.requests.PlaceConfiguredOrderRequest;

@Mapper(componentModel = "spring")
public interface PlaceConfiguredOrderMapper {
    PlaceConfiguredOrderCommand toCommand(PlaceConfiguredOrderRequest request);
}
