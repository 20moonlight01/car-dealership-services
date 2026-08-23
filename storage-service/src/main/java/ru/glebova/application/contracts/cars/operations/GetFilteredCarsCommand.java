package ru.glebova.application.contracts.cars.operations;

import ru.glebova.domain.filters.CarFilter;

public record GetFilteredCarsCommand(CarFilter[] filters) { }
