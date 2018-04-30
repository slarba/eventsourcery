package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class OrderDispatched extends Event {
    private OrderId orderId;

    protected OrderDispatched() {}

    public OrderDispatched(OrderId orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getType() {
        return "OrderDispatched";
    }
}
