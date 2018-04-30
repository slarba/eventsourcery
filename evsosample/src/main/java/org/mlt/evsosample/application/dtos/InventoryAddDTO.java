package org.mlt.evsosample.application.dtos;

import java.util.UUID;

public class InventoryAddDTO {
    private UUID productId;
    private int amount;

    public UUID getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }
}
