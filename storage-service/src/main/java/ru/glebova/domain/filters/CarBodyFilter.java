package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.enums.CarBody;

import java.util.List;

public class CarBodyFilter implements CarFilter {
    private final CarBody body;

    public CarBodyFilter(CarBody body) {
        this.body = body;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> body.equals(x.getModel().getBody()))
                .toList();
    }
}
