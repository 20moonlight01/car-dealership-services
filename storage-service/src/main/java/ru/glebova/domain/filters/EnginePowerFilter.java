package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.valueobjects.Power;

import java.util.List;

public class EnginePowerFilter implements CarFilter {
    private final Power minPower;
    private final Power maxPower;

    public EnginePowerFilter(Power minPower, Power maxPower) {
        this.minPower = minPower;
        this.maxPower = maxPower;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x
                -> minPower.isLessOrEqual(x.getModel().getEngine().getPower())
                && maxPower.isGreaterOrEqual(x.getModel().getEngine().getPower()))
                .toList();
    }
}
