package ru.glebova.domain.filters;

import ru.glebova.domain.cars.Car;
import ru.glebova.valueobjects.Volume;

import java.util.List;

public class EngineVolumeFilter implements CarFilter {
    private final Volume minVolume;
    private final Volume maxVolume;

    public EngineVolumeFilter(Volume minVolume, Volume maxVolume) {
        this.minVolume = minVolume;
        this.maxVolume = maxVolume;
    }

    @Override
    public List<Car> Apply(List<Car> cars) {
        return cars.stream()
                .filter(x
                        -> minVolume.isLessOrEqual(x.getModel().getEngine().getVolume())
                        && maxVolume.isGreaterOrEqual(x.getModel().getEngine().getVolume()))
                .toList();
    }
}
