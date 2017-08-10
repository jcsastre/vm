package com.jcsastre.vendingmachine.coinsdeposit;

import com.jcsastre.vendingmachine.Coin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FunctionalInterface
public interface CoinsChangeCalculator {

    Optional<List<Coin>> calculate(
        List<Coin> availableCoins,
        Integer amountInCents
    );

    CoinsChangeCalculator coinChangeCalculatorAsBiggestAsPossible =
        (List<Coin> availableCoins, Integer amountInCents) -> {

            List<Coin> requiredCoins = new ArrayList<>();

            final List<Coin> availableCoinsSorted =
                availableCoins.stream()
                    .sorted(Comparator.comparing(Coin::getValueInCents).reversed())
                    .collect(Collectors.toList());

            Integer pendingAmountToChange = amountInCents;
            for (Coin coin : availableCoinsSorted) {
                if (pendingAmountToChange >= coin.getValueInCents()) {
                    pendingAmountToChange = pendingAmountToChange - coin.getValueInCents();
                    requiredCoins.add(coin);
                }
            }

            if (pendingAmountToChange != 0)
                return Optional.empty();

            return Optional.of(requiredCoins);
        };
}
