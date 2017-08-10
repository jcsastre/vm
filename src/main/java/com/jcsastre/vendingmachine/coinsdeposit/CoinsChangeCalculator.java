package com.jcsastre.vendingmachine.coinsdeposit;

import com.jcsastre.vendingmachine.Coin;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface CoinsChangeCalculator {

    Optional<List<Coin>> calculate(
        List<Coin> availableCoins,
        Integer amountInCents
    );

    public final static CoinsChangeCalculator coinChangeCalculatorAsBiggestAsPossible =
        (List<Coin> availableCoins, Integer amountInCents) -> {
            return Optional.empty();
        };
}