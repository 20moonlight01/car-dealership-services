package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.models.CarModelDto;
import ru.glebova.domain.cars.CarModel;

@Mapper(
        componentModel = "spring",
        uses = {
                EngineMapper.class,
                CarPartsConfigurationMapper.class})
public interface CarModelMapper {
    @Mapping(source = "standardPrice.value", target = "standardPrice")
    @Mapping(source = "engine", target = "engineDto")
    @Mapping(source = "baseConfiguration", target = "baseConfigurationDto")
    CarModelDto toDto(CarModel model);
}
