package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;

import java.util.List;

public interface CarFilter {
    List<Car> Apply(List<Car> cars);
}
