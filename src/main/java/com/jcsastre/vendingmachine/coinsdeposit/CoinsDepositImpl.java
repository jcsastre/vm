package com.jcsastre.vendingmachine.coinsdeposit;

import com.jcsastre.vendingmachine.Coin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoinsDepositImpl implements CoinsDeposit {

    private List<Coin> coins = new ArrayList<>();

    private CoinsChangeCalculator coinsChangeCalculator;

    public CoinsDepositImpl(CoinsChangeCalculator coinsChangeCalculator) {
        this.coinsChangeCalculator = coinsChangeCalculator;
    }

    @Override
    public void insertCoin(Coin coin) {

        coins.add(coin);
    }

    @Override
    public Optional<List<Coin>> tryToReleaseAmount(Integer amountToProvideInCents) {

        final Optional<List<Coin>> optChange =
            coinsChangeCalculator.calculate(
                coins,
                amountToProvideInCents
            );

        if (optChange.isPresent()) {
            final List<Coin> coinsRequiredForChange = optChange.get();
            coins.removeAll(coinsRequiredForChange);
            return Optional.of(coinsRequiredForChange);
        }

        return Optional.empty();
    }

    @Override
    public void setCoins(List<Coin> coins) {

        this.coins = coins;
    }
}
