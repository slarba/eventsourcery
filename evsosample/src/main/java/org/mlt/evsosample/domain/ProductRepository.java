package org.mlt.evsosample.domain;

public interface ProductRepository {
    Product findById(ProductId id);
}
