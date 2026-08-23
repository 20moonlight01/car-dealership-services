package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.models.StockCarPartDto;
import ru.glebova.domain.carparts.StockCarPart;

@Mapper(componentModel = "spring")
public interface StockCarPartMapper {
    @Mapping(source = "carPart.id", target = "carPartId")
    StockCarPartDto toDto(StockCarPart stockCarPart);
}
