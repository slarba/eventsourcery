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

    public Customer getCustomer() {
        return customer;
    }

    public OrderId getOrderId() {
        return orderId;
    }
}
