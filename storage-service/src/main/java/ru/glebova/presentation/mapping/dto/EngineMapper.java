package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.models.EngineDto;
import ru.glebova.domain.carparts.Engine;

@Mapper(componentModel = "spring")
public interface EngineMapper {
    @Mapping(source = "power.value", target = "power")
    @Mapping(source = "volume.value", target = "volume")
    EngineDto toDto(Engine engine);
}
