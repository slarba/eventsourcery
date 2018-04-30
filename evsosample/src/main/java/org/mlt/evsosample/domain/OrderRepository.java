package org.mlt.evsosample.domain;

public interface OrderRepository {
    Order findById(OrderId id);
}
