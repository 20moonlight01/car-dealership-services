package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.OrderCore;

@Entity
@DiscriminatorValue("READY")
public class ReadyState extends OrderStateBase {
    @Override
    public boolean tryFinish(FinishedState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }
}
