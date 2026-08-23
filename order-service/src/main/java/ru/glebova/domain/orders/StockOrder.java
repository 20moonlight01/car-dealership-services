package ru.glebova.domain.orders;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STOCK")
public class StockOrder extends OrderBase {
    protected StockOrder() { }

    public StockOrder(OrderCore orderCore) {
        super(orderCore);
    }
}
