package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.enums.Drive;

import java.util.List;

public class DriveFilter implements CarFilter {
    private final Drive drive;

    public DriveFilter(Drive drive) {
        this.drive = drive;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x -> drive.equals(x.getConfiguration().getTransmission().getDrive()))
                .toList();
    }
}
