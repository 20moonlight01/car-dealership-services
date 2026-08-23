package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.glebova.application.contracts.cars.models.CarDto;
import ru.glebova.domain.cars.Car;

@Mapper(
        componentModel = "spring",
        uses = {
                CarModelMapper.class,
                CarPartsConfigurationMapper.class})
public interface CarMapper {
    @Mapping(source = "model", target = "modelDto")
    @Mapping(source = "configuration", target = "configurationDto")
    @Mapping(source = "car", target = "price", qualifiedByName = "getPrice")
    CarDto toDto(Car car);

    @Named("getPrice")
    default float getPrice(Car car) {
        return car.getPrice().value();
    }
}
