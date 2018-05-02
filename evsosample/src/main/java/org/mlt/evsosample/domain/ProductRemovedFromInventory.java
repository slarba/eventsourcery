package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class ProductRemovedFromInventory extends Event {
    private final ProductId productId;
    private final int amount;
    private int productUnitPrice;

    public ProductRemovedFromInventory(ProductId productId, int productUnitPrice, int amount) {
        this.productId = productId;
        this.amount = amount;
        this.productUnitPrice = productUnitPrice;
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }

    public int getProductUnitPrice() {
        return productUnitPrice;
    }
}
