package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.valueobjects.Price;

import java.util.List;

public class PriceFilter implements CarFilter {
    private final Price minPrice;
    private final Price maxPrice;

    public PriceFilter(Price minPrice, Price maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x
                        -> minPrice.isLessOrEqual(x.getPrice())
                        && maxPrice.isGreaterOrEqual(x.getPrice()))
                .toList();
    }
}
