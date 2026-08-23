package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.OrderCore;

@Entity
@DiscriminatorValue("PLACED")
public class PlacedState extends OrderStateBase {
    @Override
    public boolean tryApprove(ApprovedState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }

    @Override
    public boolean tryCancel(CancelledState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }
}
