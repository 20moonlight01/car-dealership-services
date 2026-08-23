package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.assembly.operations.AddAssemblyOrderCommand;
import ru.glebova.presentation.requests.AddAssemblyOrderRequest;

@Mapper(componentModel = "spring")
public interface AddAssemblyOrderMapper {
    AddAssemblyOrderCommand toCommand(AddAssemblyOrderRequest request);
}
