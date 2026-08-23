package ru.glebova.presentation.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.glebova.enums.CarBody;
import ru.glebova.enums.Fuel;

import java.util.UUID;

public record AddCarModelRequest(
        @NotBlank
        String name,

        @NotBlank
        String brandName,

        @NotNull
        @Positive
        float standardPrice,

        @NotNull
        CarBody body,

        @NotNull
        EngineData engine,

        @NotNull
        @Size(min = BASE_CONFIG_SIZE, max = BASE_CONFIG_SIZE)
        UUID[] baseConfiguration)
{
    private static final int BASE_CONFIG_SIZE = 4;

    public record EngineData(
            @NotNull
            Fuel fuel,

            @NotNull
            @Positive
            float power,

            @NotNull
            @Positive
            float volume)
    { }
}
