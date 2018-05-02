package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class OrderCancelled extends Event {
    private OrderId orderId;

    protected OrderCancelled() {}

    public OrderCancelled(OrderId id) {
        this.orderId = id;
    }
}
