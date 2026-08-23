package ru.glebova.application.contracts.cars.models;

import ru.glebova.enums.Drive;
import ru.glebova.enums.GearBox;

import java.util.UUID;

public abstract class CarPartDto {
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    protected final UUID id;
    protected final String name;
    protected final float price;

    protected CarPartDto(UUID id, String name, float price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static final class SteeringWheelDto extends CarPartDto {
        public SteeringWheelDto(UUID id, String name, float price) {
            super(id, name, price);
        }
    }

    public static final class WheelsDto extends CarPartDto {
        public WheelsDto(UUID id, String name, float price) {
            super(id, name, price);
        }
    }

    public static final class InteriorDto extends CarPartDto {
        public InteriorDto(UUID id, String name, float price) {
            super(id, name, price);
        }
    }

    public static final class TransmissionDto extends CarPartDto {
        public GearBox getGearBox() {
            return gearBox;
        }

        public Drive getDrive() {
            return drive;
        }

        private final GearBox gearBox;
        private final Drive drive;

        public TransmissionDto(UUID id, String name, float price, GearBox gearBox, Drive drive) {
            super(id, name, price);
            this.gearBox = gearBox;
            this.drive = drive;
        }
    }
}
