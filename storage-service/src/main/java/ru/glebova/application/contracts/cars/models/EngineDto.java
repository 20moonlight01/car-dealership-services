package ru.glebova.application.contracts.cars.models;

import ru.glebova.enums.Fuel;

public record EngineDto(Fuel fuel, float power, float volume) { }
