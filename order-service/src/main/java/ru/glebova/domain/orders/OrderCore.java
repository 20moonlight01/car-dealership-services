package ru.glebova.domain.orders;

import jakarta.persistence.*;
import ru.glebova.domain.orders.states.OrderStateBase;
import ru.glebova.valueobjects.Price;

import java.util.UUID;

@Embeddable
public class OrderCore {
    public OrderStateBase getState() {
        return state;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public UUID getCarId() {
        return carId;
    }

    public void setState(OrderStateBase state) {
        this.state = state;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    @ManyToOne
    @JoinColumn(name = "state_id")
    private OrderStateBase state;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "value",
                    column = @Column(name = "price", nullable = false))})
    private Price price;

    protected OrderCore() { }

    public OrderCore(UUID clientId, UUID managerId, UUID carId, OrderStateBase state, Price price) {
        this.clientId = clientId;
        this.managerId = managerId;
        this.carId = carId;
        this.state = state;
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
