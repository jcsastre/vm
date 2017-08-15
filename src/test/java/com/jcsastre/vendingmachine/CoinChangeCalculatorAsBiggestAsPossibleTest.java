package com.jcsastre.vendingmachine;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CoinChangeCalculatorAsBiggestAsPossibleTest {

    private ArrayList<Coin> availableCoins;

    @Before
    public void setUp() {

        availableCoins = new ArrayList<>(Arrays.asList(
            Coin.TWO_EUROS,
            Coin.FIFTY_CENTS,
            Coin.ONE_EURO
        ));
    }

    @Test
    public void shouldReturnValidChange() {

        // When
        final Optional<List<Coin>> change =
            CoinsChangeCalculator.coinChangeCalculatorAsBiggestAsPossible.calculate(
                availableCoins,
                Coin.TWO_EUROS.getValueInCents() + Coin.FIFTY_CENTS.getValueInCents()
            );

        // Then
        assertThat(change, is(Optional.of(Arrays.asList(Coin.TWO_EUROS, Coin.FIFTY_CENTS))));
    }

    @Test
    public void shouldReturnEmpty() {

        // When
        final Optional<List<Coin>> change =
            CoinsChangeCalculator.coinChangeCalculatorAsBiggestAsPossible.calculate(
                availableCoins,
                Coin.TWO_EUROS.getValueInCents() + Coin.TWO_EUROS.getValueInCents()
            );

        // Then
        assertThat(change, is(Optional.empty()));
    }
}
