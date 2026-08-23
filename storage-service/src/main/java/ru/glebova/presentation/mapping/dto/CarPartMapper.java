package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.models.CarPartDto;
import ru.glebova.domain.carparts.CarPart;

@Mapper(componentModel = "spring")
public interface CarPartMapper {
    @Mapping(source = "price.value", target = "price")
    CarPartDto.SteeringWheelDto toDto(CarPart.SteeringWheel steeringWheel);

    @Mapping(source = "price.value", target = "price")
    CarPartDto.WheelsDto toDto(CarPart.Wheels wheels);

    @Mapping(source = "price.value", target = "price")
    CarPartDto.InteriorDto toDto(CarPart.Interior interior);

    @Mapping(source = "price.value", target = "price")
    CarPartDto.TransmissionDto toDto(CarPart.Transmission transmission);

    default CarPartDto toDto(CarPart part) {
        return switch (part) {
            case CarPart.SteeringWheel castPart -> toDto(castPart);
            case CarPart.Wheels castPart -> toDto(castPart);
            case CarPart.Interior castPart -> toDto(castPart);
            case CarPart.Transmission castPart -> toDto(castPart);
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
