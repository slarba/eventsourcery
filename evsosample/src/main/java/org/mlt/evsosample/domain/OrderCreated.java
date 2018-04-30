package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class OrderCreated extends Event {
    private OrderId orderId;
    private Customer customer;

    protected OrderCreated() {}

    public OrderCreated(OrderId id, Customer customer) {
        this.orderId = id;
        this.customer = customer;
    }

    @Override
    public String getType() {
        return "OrderCreated";
    }
}
