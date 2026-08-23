package ru.glebova.domain.orders;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;
import ru.glebova.domain.orders.states.*;
import ru.glebova.valueobjects.Price;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "order_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "orders")
@SQLRestriction("removed = false")
public abstract class OrderBase extends EntityBase {
    public OrderCore getOrderCore() {
        return orderCore;
    }

    public void setOrder(OrderCore orderCore) {
        this.orderCore = orderCore;
    }

    @Embedded
    protected OrderCore orderCore;

    protected OrderBase() { }

    public OrderBase(OrderCore orderCore) {
        this.orderCore = orderCore;
    }

    public OrderStateBase getState() { return orderCore.getState(); }

    public UUID getClientId() {
        return orderCore.getClientId();
    }

    public UUID getManagerId() {
        return orderCore.getManagerId();
    }

    public UUID getCarId() {
        return orderCore.getCarId();
    }

    public Price getPrice() { return orderCore.getPrice(); }

    public boolean tryApprove(ApprovedState state) {
        return orderCore.getState().tryApprove(state, orderCore);
    }

    public boolean tryMarkAwaitingPay(AwaitingPayState state) {
        return orderCore.getState().tryMarkAwaitingPay(state, orderCore);
    }

    public boolean tryPay(PayedState state, Price payment) {
        return orderCore.getState().tryPay(state, orderCore, payment);
    }

    public boolean tryMarkReady(ReadyState state) {
        return orderCore.getState().tryMarkReady(state, orderCore);
    }

    public boolean tryFinish(FinishedState state) {
        return orderCore.getState().tryFinish(state, orderCore);
    }

    public boolean tryCancel(CancelledState state) {
        return orderCore.getState().tryCancel(state, orderCore);
    }
}

