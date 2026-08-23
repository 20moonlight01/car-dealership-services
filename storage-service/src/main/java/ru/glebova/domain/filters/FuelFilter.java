package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.enums.Fuel;

import java.util.List;

public class FuelFilter implements CarFilter {
    private final Fuel fuel;

    public FuelFilter(Fuel fuel) {
        this.fuel = fuel;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> fuel.equals(x.getModel().getEngine().getFuel()))
                .toList();
    }
}
