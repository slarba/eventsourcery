package org.mlt.evsosample.domain;

import org.mlt.eso.Identity;

import java.util.UUID;

public class ProductId extends Identity {
    public ProductId() {
    }

    public ProductId(String id) {
        super(id);
    }

    public ProductId(UUID id) {
        super(id);
    }
}
