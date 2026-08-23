package ru.glebova.presentation.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.glebova.enums.Drive;
import ru.glebova.enums.GearBox;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddCarPartRequest.AddSteeringWheelRequest.class, name = "STEERING_WHEEL"),
        @JsonSubTypes.Type(value = AddCarPartRequest.AddWheelsRequest.class, name = "WHEELS"),
        @JsonSubTypes.Type(value = AddCarPartRequest.AddInteriorRequest.class, name = "INTERIOR"),
        @JsonSubTypes.Type(value = AddCarPartRequest.AddTransmissionRequest.class, name = "TRANSMISSION")})
public abstract class AddCarPartRequest {
    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public UUID[] getModelIds() {
        return modelIds;
    }

    @NotBlank
    protected String name;

    @NotNull
    @Positive
    protected float price;

    protected UUID[] modelIds;

    public AddCarPartRequest(String name, float price, UUID[] modelIds) {
        this.name = name;
        this.price = price;
        this.modelIds = modelIds;
    }

    public static class AddSteeringWheelRequest extends AddCarPartRequest {
        public AddSteeringWheelRequest(String name, float price, UUID[] modelIds) {
            super(name, price, modelIds);
        }
    }

    public static class AddWheelsRequest extends AddCarPartRequest {
        public AddWheelsRequest(String name, float price, UUID[] modelIds) {
            super(name, price, modelIds);
        }
    }

    public static class AddInteriorRequest extends AddCarPartRequest {
        public AddInteriorRequest(String name, float price, UUID[] modelIds) {
            super(name, price, modelIds);
        }
    }

    public static class AddTransmissionRequest extends AddCarPartRequest {
        public GearBox getGearBox() {
            return gearBox;
        }

        public Drive getDrive() {
            return drive;
        }

        @NotNull
        private final GearBox gearBox;

        @NotNull
        private final Drive drive;

        public AddTransmissionRequest(String name, float price, UUID[] modelIds, GearBox gearBox, Drive drive) {
            super(name, price, modelIds);
            this.gearBox = gearBox;
            this.drive = drive;
        }
    }
}
