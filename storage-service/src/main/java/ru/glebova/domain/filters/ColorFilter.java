package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;

import java.util.List;

public class ColorFilter implements CarFilter {
    private final String color;

    public ColorFilter(String color) {
        this.color = color;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> color.equals(x.getColor()))
                .toList();
    }
}
