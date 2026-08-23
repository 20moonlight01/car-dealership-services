package ru.glebova.domain.cars;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;

@Entity
@Table(name = "testable_cars")
@SQLRestriction("removed = false")
public class TestableCar extends EntityBase {
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    protected TestableCar() { }

    public TestableCar(Car car) {
        this.car = car;
    }
}
