package ru.glebova.application.contracts.cars.operations;

import ru.glebova.enums.CarBody;
import ru.glebova.enums.Fuel;

import java.util.UUID;

public record AddCarModelCommand(
        String name,
        String brandName,
        float standardPrice,
        CarBody body,
        Fuel fuel,
        float power,
        float volume,
        UUID[] baseConfiguration)
{ }
