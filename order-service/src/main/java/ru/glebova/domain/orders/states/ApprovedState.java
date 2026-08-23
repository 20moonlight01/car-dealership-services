package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.OrderCore;

@Entity
@DiscriminatorValue("APPROVED")
public class ApprovedState extends OrderStateBase {
    @Override
    public boolean tryMarkAwaitingPay(AwaitingPayState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }

    @Override
    public boolean tryCancel(CancelledState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }
}
