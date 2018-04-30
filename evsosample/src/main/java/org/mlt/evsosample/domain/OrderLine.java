package org.mlt.evsosample.domain;

public class OrderLine {
    private final Product product;
    private final int amount;

    public OrderLine(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public int getAmount() {
        return amount;
    }
}
