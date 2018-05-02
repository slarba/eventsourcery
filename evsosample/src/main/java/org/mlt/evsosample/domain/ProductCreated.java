package org.mlt.evsosample.domain;

import org.mlt.eso.Event;

public class ProductCreated extends Event {
    private ProductId productId;
    private String name;
    private int unitPrice;

    protected ProductCreated() {}

    public ProductCreated(ProductId productId, String name, int unitPrice) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }
}
