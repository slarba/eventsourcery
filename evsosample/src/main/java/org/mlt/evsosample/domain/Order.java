package org.mlt.evsosample.domain;

import org.mlt.eso.Aggregate;
import org.mlt.eso.Events;

import java.util.ArrayList;
import java.util.List;

public class Order extends Aggregate<OrderId> {
    private Customer customer;
    private boolean isDispatched;
    private List<OrderLine> orderLines = new ArrayList<>();

    public Order() {}

    public Order(Customer customer) {
        super(new OrderId());
        this.customer = customer;
        this.isDispatched = false;
        Events.dispatch(this, new OrderCreated(getId(), customer));
    }

    public void on(OrderCreated event) {
        setId(event.getOrderId());
        customer = event.getCustomer();
    }

    public void on(OrderDispatched event) {
        isDispatched = true;
    }

    public void on(OrderLineAdded event) {
        orderLines.add(new OrderLine(event.getProduct(), event.getAmount()));
    }

    public void addOrderLine(Product product, int amount) {
        orderLines.add(new OrderLine(product, amount));
        Events.dispatch(this, new OrderLineAdded(getId(), product, amount));
    }

    public void dispatch() {
        isDispatched = true;
        Events.dispatch(this, new OrderDispatched());
    }
}
