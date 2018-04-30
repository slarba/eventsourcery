package org.mlt.evsosample.domain;

import org.mlt.eso.Aggregate;
import org.mlt.eso.Events;

public class Product extends Aggregate<ProductId> {
    private int unitPrice;
    private String name;
    private boolean isInInventory;

    public Product() {}

    public Product(String name, int unitPrice) {
        super(new ProductId());
        this.name = name;
        this.unitPrice = unitPrice;
        Events.dispatch(this, new ProductCreated(getId(), name, unitPrice));
    }

    public void on(ProductCreated event) {
        setId(event.getProductId());
        name = event.getName();
        unitPrice = event.getUnitPrice();
    }

    public void on(ProductAddedToInventory event) {
        // nothing to do here
        isInInventory = true;
    }

    public void addToInventory(int amount) {
        isInInventory = true;
        Events.dispatch(this, new ProductAddedToInventory(getId(), amount));
    }
}
