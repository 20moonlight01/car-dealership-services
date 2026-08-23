package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;

import java.util.List;

public class ModelFilter implements CarFilter {
    private final String modelName;

    public ModelFilter(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> modelName.equals(x.getModel().getName()))
                .toList();
    }
}
