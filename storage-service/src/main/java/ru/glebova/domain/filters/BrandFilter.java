package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;

import java.util.List;

public class BrandFilter implements CarFilter {
    private final String brandName;

    public BrandFilter(String brandName) {
        this.brandName = brandName;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> brandName.equals(x.getModel().getBrandName()))
                .toList();
    }
}
