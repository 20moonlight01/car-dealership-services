package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.cars.operations.AddCarPartCommand;
import ru.glebova.presentation.requests.AddCarPartRequest;

@Mapper(componentModel = "spring")
public interface AddCarPartMapper {
    AddCarPartCommand.AddSteeringWheelCommand toCommand(AddCarPartRequest.AddSteeringWheelRequest request);

    AddCarPartCommand.AddWheelsCommand toCommand(AddCarPartRequest.AddWheelsRequest request);

    AddCarPartCommand.AddInteriorCommand toCommand(AddCarPartRequest.AddInteriorRequest request);

    AddCarPartCommand.AddTransmissionCommand toCommand(AddCarPartRequest.AddTransmissionRequest request);

    default AddCarPartCommand toCommand(AddCarPartRequest request) {
        return switch (request) {
            case AddCarPartRequest.AddSteeringWheelRequest castRequest -> toCommand(castRequest);
            case AddCarPartRequest.AddWheelsRequest castRequest -> toCommand(castRequest);
            case AddCarPartRequest.AddInteriorRequest castRequest -> toCommand(castRequest);
            case AddCarPartRequest.AddTransmissionRequest castRequest -> toCommand(castRequest);
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
