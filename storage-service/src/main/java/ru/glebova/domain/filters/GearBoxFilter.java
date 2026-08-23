package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.enums.GearBox;

import java.util.List;

public class GearBoxFilter implements CarFilter {
    private final GearBox gearBox;

    public GearBoxFilter(GearBox gearBox) {
        this.gearBox = gearBox;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> gearBox.equals(x.getConfiguration().getTransmission().getGearBox()))
                .toList();
    }
}
