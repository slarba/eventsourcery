package org.mlt.evsosample.application.dtos;

import java.util.UUID;

public class ProductDTO {
    private UUID id;
    private String name;
    private int unitPrice;

    protected ProductDTO() {
    }

    public ProductDTO(UUID id, String name, int unitPrice) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public UUID getId() {
        return id;
    }
}
