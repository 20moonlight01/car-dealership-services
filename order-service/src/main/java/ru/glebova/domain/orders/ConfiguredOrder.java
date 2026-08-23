package ru.glebova.domain.orders;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.states.DeliveringState;

@Entity
@DiscriminatorValue("CONFIGURED")
public class ConfiguredOrder extends OrderBase {
    protected ConfiguredOrder() { }

    public ConfiguredOrder(OrderCore orderCore) {
        super(orderCore);
    }

    public boolean tryMarkDelivering(DeliveringState state) {
        return orderCore.getState().tryMarkDelivering(state, orderCore);
    }
}
