package ru.glebova.domain.carparts;

import jakarta.persistence.*;
import ru.glebova.enums.Fuel;
import ru.glebova.valueobjects.Power;
import ru.glebova.valueobjects.Volume;

@Embeddable
public class Engine {
    protected Engine() { }

    public Engine(Fuel fuel, Power power, Volume volume) {
        this.fuel = fuel;
        this.power = power;
        this.volume = volume;
    }

    public Fuel getFuel() {
        return fuel;
    }

    public Power getPower() {
        return power;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel", nullable = false)
    private Fuel fuel;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "value",
                    column = @Column(name = "engine_power", nullable = false))})
    private Power power;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "value",
                    column = @Column(name = "engine_volume", nullable = false))})
    private Volume volume;
}
