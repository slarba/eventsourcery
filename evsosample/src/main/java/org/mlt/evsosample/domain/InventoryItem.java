package org.mlt.evsosample.domain;

public class InventoryItem {
    private ProductId productId;
    private final int amount;

    public InventoryItem(ProductId id, int amount) {
        this.productId = id;
        this.amount = amount;
    }
}
