package ru.glebova.presentation.mapping.commands;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.cars.operations.GetFilteredCarsCommand;
import ru.glebova.domain.filters.*;
import ru.glebova.presentation.requests.GetFilteredCarsRequest;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public interface GetFilteredCarsMapper {
    BrandFilter toFilter(GetFilteredCarsRequest.BrandFilterData filterData);
    CarBodyFilter toFilter(GetFilteredCarsRequest.CarBodyFilterData filterData);
    ColorFilter toFilter(GetFilteredCarsRequest.ColorFilterData filterData);
    DriveFilter toFilter(GetFilteredCarsRequest.DriveFilterData filterData);

    @Mapping(source = "minPower", target = "minPower.value")
    @Mapping(source = "maxPower", target = "maxPower.value")
    EnginePowerFilter toFilter(GetFilteredCarsRequest.EnginePowerFilterData filterData);

    @Mapping(source = "minVolume", target = "minVolume.value")
    @Mapping(source = "maxVolume", target = "maxVolume.value")
    EngineVolumeFilter toFilter(GetFilteredCarsRequest.EngineVolumeFilterData filterData);

    FuelFilter toFilter(GetFilteredCarsRequest.FuelFilterData filterData);
    GearBoxFilter toFilter(GetFilteredCarsRequest.GearBoxFilterData filterData);
    ModelFilter toFilter(GetFilteredCarsRequest.ModelFilterData filterData);

    @Mapping(source = "minPrice", target = "minPrice.value")
    @Mapping(source = "maxPrice", target = "maxPrice.value")
    PriceFilter toFilter(GetFilteredCarsRequest.PriceFilterData filterData);

    default CarFilter toFilter(GetFilteredCarsRequest.CarFilterData filterData) {
        return switch (filterData) {
            case GetFilteredCarsRequest.BrandFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.CarBodyFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.ColorFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.DriveFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.EnginePowerFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.EngineVolumeFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.FuelFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.GearBoxFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.ModelFilterData castFilter -> toFilter(castFilter);
            case GetFilteredCarsRequest.PriceFilterData castFilter -> toFilter(castFilter);
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }

    default CarFilter[] toFilters(GetFilteredCarsRequest.CarFilterData[] filtersData) {
        return Arrays.stream(filtersData).map(this::toFilter).toArray(CarFilter[]::new);
    }

    @Mapping(source = "carFilters", target = "filters")
    GetFilteredCarsCommand toCommand(GetFilteredCarsRequest request);
}
