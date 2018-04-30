package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class ProductRemovedFromInventory extends Event {
    private final ProductId productId;
    private final int amount;

    public ProductRemovedFromInventory(ProductId productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "ProductRemovedFromInventory";
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }
}
