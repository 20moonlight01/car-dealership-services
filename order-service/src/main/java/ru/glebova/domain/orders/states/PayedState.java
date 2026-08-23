package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.OrderCore;

@Entity
@DiscriminatorValue("PAYED")
public class PayedState extends OrderStateBase {
    @Override
    public boolean tryMarkDelivering(DeliveringState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }

    @Override
    public boolean tryMarkReady(ReadyState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }

    @Override
    public boolean tryCancel(CancelledState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }
}
