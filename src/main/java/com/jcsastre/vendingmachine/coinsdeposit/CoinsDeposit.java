package com.jcsastre.vendingmachine.coinsdeposit;

import com.jcsastre.vendingmachine.Coin;

import java.util.List;
import java.util.Optional;

/**
 * <p>Interface representing a deposit of coins.</p>
 *
 * <p>That deposit has no limit capacity.</p>
 *
 * @author Juan Carlos Sastre
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
     * <p>Replaces the current deposited coins.</p>
     *
     * @param coins The list of coins being the new deposited coins.
     */
    void setCoins(List<Coin> coins);
}
