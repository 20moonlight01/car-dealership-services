package ru.glebova.presentation.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.glebova.enums.CarBody;
import ru.glebova.enums.Drive;
import ru.glebova.enums.Fuel;
import ru.glebova.enums.GearBox;

public record GetFilteredCarsRequest(
        @NotNull
        CarFilterData[] carFilters)
{
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BrandFilterData.class, name = "BRAND"),
            @JsonSubTypes.Type(value = CarBodyFilterData.class, name = "BODY"),
            @JsonSubTypes.Type(value = ColorFilterData.class, name = "COLOR"),
            @JsonSubTypes.Type(value = DriveFilterData.class, name = "DRIVE"),
            @JsonSubTypes.Type(value = EnginePowerFilterData.class, name = "ENGINE_POWER"),
            @JsonSubTypes.Type(value = EngineVolumeFilterData.class, name = "ENGINE_VOLUME"),
            @JsonSubTypes.Type(value = FuelFilterData.class, name = "FUEL"),
            @JsonSubTypes.Type(value = GearBoxFilterData.class, name = "GEARBOX"),
            @JsonSubTypes.Type(value = ModelFilterData.class, name = "MODEL"),
            @JsonSubTypes.Type(value = PriceFilterData.class, name = "PRICE")})
    public interface CarFilterData {}

    public record BrandFilterData(
            @NotBlank
            String brandName)
            implements CarFilterData
    { }

    public record CarBodyFilterData(
            @NotNull
            CarBody body)
            implements CarFilterData
    { }

    public record ColorFilterData(
            @NotBlank
            String color)
            implements CarFilterData
    { }

    public record DriveFilterData(
            @NotNull
            Drive drive)
            implements CarFilterData
    { }

    public record EnginePowerFilterData(
            @NotNull
            @Positive
            float minPower,

            @NotNull
            @Positive
            float maxPower)
            implements CarFilterData
    { }

    public record EngineVolumeFilterData(
            @NotNull
            @Positive
            float minVolume,

            @NotNull
            @Positive
            float maxVolume)
            implements CarFilterData
    { }

    public record FuelFilterData(
            @NotNull
            Fuel fuel)
            implements CarFilterData
    { }

    public record GearBoxFilterData(
            @NotNull
            GearBox gearBox)
            implements CarFilterData
    { }

    public record ModelFilterData(
            @NotBlank
            String modelName)
            implements CarFilterData
    { }

    public record PriceFilterData(
            @NotNull
            @Positive
            float minPrice,

            @NotNull
            @Positive
            float maxPrice)
            implements CarFilterData
    { }
}
