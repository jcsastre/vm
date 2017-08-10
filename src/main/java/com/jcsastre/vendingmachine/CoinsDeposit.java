package com.jcsastre.vendingmachine;

import java.util.List;
import java.util.Optional;

/**
 * <p>Interface representing a deposit of coins.</p>
 *
 * <p>That deposit has no limit capacity.</p>
 */
public interface CoinsDeposit {

    /**
     *  <p>Accepts a coin.</p>
     *
     * @param coin Coin to insert.
     */
    void insertCoin(Coin coin);

    /**
     * <p>Tries to release the specified amount based on deposited coins.</p>
     *
     * @param amountToProvideInCents Amount to provide as coins.
     * @return The list of coins for the amount, or Optional.empty()
     */
    Optional<List<Coin>> tryToReleaseAmount(Integer amountToProvideInCents);

    /**
     * <p>Sets the deposited coins replacing the previous ones.</p>
     *
     * @param coins The list of coins being the new current state of deposited coins.
     */
    void setCoins(List<Coin> coins);
}
