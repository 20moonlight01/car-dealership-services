package ru.glebova.domain.orders.states;

import jakarta.persistence.*;
import ru.glebova.domain.EntityBase;
import ru.glebova.domain.orders.OrderCore;
import ru.glebova.valueobjects.Price;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "state_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "order_states")
public abstract class OrderStateBase extends EntityBase {
    public boolean tryApprove(ApprovedState state, OrderCore orderCore) { return false; }
    public boolean tryMarkAwaitingPay(AwaitingPayState state, OrderCore orderCore) { return false; }
    public boolean tryPay(PayedState state, OrderCore orderCore, Price payment) { return false; }
    public boolean tryMarkDelivering(DeliveringState state, OrderCore orderCore) { return false; }
    public boolean tryMarkReady(ReadyState state, OrderCore orderCore) { return false; }
    public boolean tryFinish(ru.glebova.domain.orders.states.FinishedState state, OrderCore orderCore) { return false; }
    public boolean tryCancel(CancelledState state, OrderCore orderCore) { return false; }
}
