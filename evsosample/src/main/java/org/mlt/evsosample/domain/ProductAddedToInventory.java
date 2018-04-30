package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class ProductAddedToInventory extends Event {
    private int amount;
    private ProductId productId;

    protected ProductAddedToInventory() {}

    public ProductAddedToInventory(ProductId product, int amount) {
        this.productId = product;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "ProductAddedToInventory";
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }
}
