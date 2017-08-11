package com.jcsastre.vendingmachine;

public enum Coin {

    FIVE_CENTS (5),
    TEN_CENTS (10),
    TWENTY_CENTS (20),
    FIFTY_CENTS (50),
    ONE_EURO (100),
    TWO_EUROS (200);

    private Integer valueInCents;

    Coin(Integer valueInCents) {
        this.valueInCents = valueInCents;
    }

    public Integer getValueInCents() {
        return valueInCents;
    }
}
