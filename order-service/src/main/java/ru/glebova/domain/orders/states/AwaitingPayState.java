package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.glebova.domain.orders.OrderCore;
import ru.glebova.valueobjects.Price;

@Entity
@DiscriminatorValue("AWAITING_PAY")
public class AwaitingPayState extends OrderStateBase {
    @Override
    public boolean tryPay(PayedState state, OrderCore orderCore, Price payment) {
        if (!payment.isGreaterOrEqual(orderCore.getPrice()))
            return false;

        orderCore.setState(state);
        return true;
    }

    @Override
    public boolean tryCancel(CancelledState state, OrderCore orderCore) {
        orderCore.setState(state);
        return true;
    }
}
