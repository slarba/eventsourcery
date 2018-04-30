package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class OrderLineAdded extends Event {
    private Product product;
    private int amount;
    private OrderId orderId;

    protected OrderLineAdded() {}

    public OrderLineAdded(OrderId orderId, Product product, int amount) {
        this.orderId = orderId;
        this.product = product;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "OrderLineAdded";
    }

    public int getAmount() {
        return amount;
    }

    public Product getProduct() {
        return product;
    }
}
