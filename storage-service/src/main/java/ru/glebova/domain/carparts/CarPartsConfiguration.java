package ru.glebova.domain.carparts;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import ru.glebova.valueobjects.Price;

@Embeddable
public class CarPartsConfiguration {
    public CarPart.SteeringWheel getSteeringWheel() {
        return steeringWheel;
    }

    public CarPart.Wheels getWheels() {
        return wheels;
    }

    public CarPart.Interior getInterior() {
        return interior;
    }

    public CarPart.Transmission getTransmission() {
        return transmission;
    }

    @ManyToOne
    @JoinColumn(name = "steering_wheel_id", nullable = false)
    private CarPart.SteeringWheel steeringWheel;

    @ManyToOne
    @JoinColumn(name = "wheels_id", nullable = false)
    private CarPart.Wheels wheels;

    @ManyToOne
    @JoinColumn(name = "interior_id", nullable = false)
    private CarPart.Interior interior;

    @ManyToOne
    @JoinColumn(name = "transmission_id", nullable = false)
    private CarPart.Transmission transmission;

    protected CarPartsConfiguration() { }

    public CarPartsConfiguration(
            CarPart.SteeringWheel steeringWheel,
            CarPart.Wheels wheels,
            CarPart.Interior interior,
            CarPart.Transmission transmission)
    {
        this.steeringWheel = steeringWheel;
        this.wheels = wheels;
        this.interior = interior;
        this.transmission = transmission;
    }

    public void setPart(CarPart part) {
        switch (part) {
            case CarPart.SteeringWheel castPart ->
                    this.steeringWheel = castPart;
            case CarPart.Wheels castPart ->
                    this.wheels = castPart;
            case CarPart.Interior castPart ->
                    this.interior = castPart;
            case CarPart.Transmission castPart ->
                    this.transmission = castPart;
            default -> throw new IllegalArgumentException("Unknown type");
        }
    }

    public Price getTotalPrice() {
        return steeringWheel.getPrice()
                .add(wheels.getPrice())
                .add(interior.getPrice())
                .add(transmission.getPrice());
    }

    public static class Builder {
        private CarPart.SteeringWheel steeringWheel;
        private CarPart.Wheels wheels;
        private CarPart.Interior interior;
        private CarPart.Transmission transmission;

        public Builder setPart(CarPart part) {
            switch (part) {
                case CarPart.SteeringWheel castPart ->
                        this.steeringWheel = castPart;
                case CarPart.Wheels castPart ->
                        this.wheels = castPart;
                case CarPart.Interior castPart ->
                        this.interior = castPart;
                case CarPart.Transmission castPart ->
                        this.transmission = castPart;
                default -> throw new IllegalArgumentException("Unknown type");
            }

            return this;
        }

        public CarPartsConfiguration Build() {
            if (steeringWheel == null || wheels == null || interior == null || transmission == null) {
                throw new RuntimeException();
            }

            return new CarPartsConfiguration(steeringWheel, wheels, interior, transmission);
        }
    }
}
