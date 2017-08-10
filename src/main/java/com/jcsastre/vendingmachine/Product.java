package com.jcsastre.vendingmachine;

public enum Product {

    COKE (150),
    SPRITE (145),
    WATER (90);

    private int priceInCents;

    Product(int priceInCents) {
        this.priceInCents = priceInCents;
    }

    public int getPriceInCents() {
        return priceInCents;
    }
}
