package com.jcsastre.vendingmachine;

import java.util.List;
import java.util.Optional;

public interface CoinsDeposit {

    void insertCoin(Coin coin);

    Optional<List<Coin>> tryToReleaseAmount(Integer amountToProvideInCents);
}
