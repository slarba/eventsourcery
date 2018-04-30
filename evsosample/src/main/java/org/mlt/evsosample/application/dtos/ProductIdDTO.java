package org.mlt.evsosample.application.dtos;

import org.mlt.evsosample.domain.ProductId;

import java.util.UUID;

public class ProductIdDTO {
    private UUID id;

    public ProductIdDTO(ProductId id) {
        this.id = id.getUUID();
    }

    public UUID getId() {
        return id;
    }
}
