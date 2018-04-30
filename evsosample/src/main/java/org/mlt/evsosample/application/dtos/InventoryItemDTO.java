package org.mlt.evsosample.application.dtos;

import java.util.UUID;

public class InventoryItemDTO {
    private UUID productId;
    private int amount;

    public InventoryItemDTO(UUID productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }
}
