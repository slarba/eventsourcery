package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class ProductAddedToInventory extends Event {
    private int amount;
    private ProductId productId;
    private int productUnitPrice;

    protected ProductAddedToInventory() {}

    public ProductAddedToInventory(ProductId product, int productUnitPrice, int amount) {
        this.productId = product;
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
