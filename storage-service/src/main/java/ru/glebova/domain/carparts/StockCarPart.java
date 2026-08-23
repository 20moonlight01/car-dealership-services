package ru.glebova.domain.carparts;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;

import java.util.UUID;

@Entity
@Table(name = "stock_car_parts")
@SQLRestriction("removed = false")
public class StockCarPart extends EntityBase {
    @OneToOne
    @JoinColumn(name = "car_part_id", nullable = false)
    private CarPart carPart;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reserved", nullable = false)
    private int reserved;

    protected StockCarPart() { }

    public StockCarPart(CarPart carPart, int quantity, int reserved) {
        this.carPart = carPart;
        this.quantity = quantity;
        this.reserved = reserved;
    }

    public CarPart getCarPart() {
        return carPart;
    }

    public void setCarPart(CarPart carPart) {
        this.carPart = carPart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }
}
