package ru.glebova.application.contracts.cars.operations;

import ru.glebova.enums.Drive;
import ru.glebova.enums.GearBox;

import java.util.UUID;

public abstract class AddCarPartCommand {
    public AddCarPartCommand(String name, float price, UUID[] modelIds) {
        this.name = name;
        this.price = price;
        this.modelIds = modelIds;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public UUID[] getModelIds() {
        return modelIds;
    }

    protected String name;
    protected float price;
    protected UUID[] modelIds;

    public static class AddSteeringWheelCommand extends AddCarPartCommand {
        public AddSteeringWheelCommand(String name, float price, UUID[] modelIds) {
            super(name, price, modelIds);
        }
    }

    public static class AddWheelsCommand extends AddCarPartCommand {
        public AddWheelsCommand(String name, float price, UUID[] modelIds) {
            super(name, price, modelIds);
        }
    }

    public static class AddInteriorCommand extends AddCarPartCommand {
        public AddInteriorCommand(String name, float price, UUID[] modelIds) {
            super(name, price, modelIds);
        }
    }

    public static class AddTransmissionCommand extends AddCarPartCommand {
        public GearBox getGearBox() {
            return gearBox;
        }

        public Drive getDrive() {
            return drive;
        }

        private final GearBox gearBox;
        private final Drive drive;

        public AddTransmissionCommand(String name, float price, UUID[] modelIds, GearBox gearBox, Drive drive) {
            super(name, price, modelIds);
            this.gearBox = gearBox;
            this.drive = drive;
        }
    }
}
