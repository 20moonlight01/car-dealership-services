package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.models.CarPartsConfigurationDto;
import ru.glebova.domain.carparts.CarPartsConfiguration;

@Mapper(componentModel = "spring", uses = CarPartMapper.class)
public interface CarPartsConfigurationMapper {
    @Mapping(source = "steeringWheel", target = "steeringWheelDto")
    @Mapping(source = "wheels", target = "wheelsDto")
    @Mapping(source = "interior", target = "interiorDto")
    @Mapping(source = "transmission", target = "transmissionDto")
    CarPartsConfigurationDto toDto(CarPartsConfiguration configuration);
}
