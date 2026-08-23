package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.OrderCore;

@Entity
@DiscriminatorValue("DELIVERING")
public class DeliveringState extends OrderStateBase {
    @Override
    public boolean tryMarkReady(ReadyState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }
}
