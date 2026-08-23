package ru.glebova.domain.cars;


import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;
import ru.glebova.domain.carparts.CarPart;
import ru.glebova.domain.carparts.CarPartsConfiguration;
import ru.glebova.valueobjects.Price;

@Entity
@Table(name = "cars")
@SQLRestriction("removed = false")
public class Car extends EntityBase
{
    protected Car() { }

    public Car(CarModel model, String color, CarPartsConfiguration configuration, CarType type) {
        this.model = model;
        this.color = color;
        this.configuration = configuration;
        this.carType = type;
    }

    public CarModel getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public CarPartsConfiguration getConfiguration() {
        return configuration;
    }

    public void setModel(CarModel model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setConfiguration(CarPartsConfiguration configuration) {
        this.configuration = configuration;
    }

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    @Column(nullable = false)
    private String color;

    @Embedded
    private CarPartsConfiguration configuration;

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false)
    private CarType carType;

    public Price getPrice() {
        return model.getStandardPrice()
                .subtract(model.getBaseConfiguration().getTotalPrice())
                .add(configuration.getTotalPrice());
    }

    public static class Builder {
        private CarModel model;
        private String color;
        private CarPartsConfiguration configuration;
        private CarType type;

        public Builder setModel(CarModel model) {
            this.model = model;
            this.configuration = new CarPartsConfiguration(
                    model.getBaseConfiguration().getSteeringWheel(),
                    model.getBaseConfiguration().getWheels(),
                    model.getBaseConfiguration().getInterior(),
                    model.getBaseConfiguration().getTransmission());
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setPart(CarPart part) {
            this.configuration.setPart(part);
            return this;
        }

        public Builder setType(CarType type) {
            this.type = type;
            return this;
        }

        public Car build() {
            if (model == null || color == null || type == null)
                throw new RuntimeException();

            return new Car(
                    model,
                    color,
                    configuration,
                    type);
        }
    }
}
