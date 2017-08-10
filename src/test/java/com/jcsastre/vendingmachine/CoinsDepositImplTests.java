package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.Coin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class CoinsDepositImplTests {

    private ArrayList<Coin> coins;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        coins = new ArrayList<>(Arrays.asList(
            Coin.TWO_EUROS,
            Coin.ONE_EURO,
            Coin.FIFTY_CENTS
        ));
    }

    @Test
    public void Given_EmptyDeposit_When_InsertingCoin_Then_CorrectlyUpdatesDepositedCoins() {

        // Given: Empty Deposit
        final CoinsDepositImpl coinsDepositImpl = new CoinsDepositImpl();

        // When: Inserting Coin
        coinsDepositImpl.insertCoin(Coin.ONE_EURO);

        // Then: Correctly Updates Deposited Coins
        assertThat(
            Whitebox.getInternalState(coinsDepositImpl, "coins"),
            is(Collections.singletonList(Coin.ONE_EURO))
        );
    }

    @Test
    public void Given_NonEmptyDeposit_When_InsertingCoin_Then_CorrectlyUpdatesDepositedCoins() {

        // Given: Not Empty Deposit
        final CoinsDepositImpl coinsDepositImpl = new CoinsDepositImpl();
        Whitebox.setInternalState(coinsDepositImpl, "coins", coins);

        // When: Inserting Coin
        coinsDepositImpl.insertCoin(Coin.ONE_EURO);

        // Then: Correctly Updates Deposited Coins
        assertThat(
            Whitebox.getInternalState(coinsDepositImpl, "coins"),
            is(Arrays.asList(Coin.TWO_EUROS, Coin.ONE_EURO, Coin.FIFTY_CENTS, Coin.ONE_EURO))
        );
    }

    @Test
    public void Given_CoinsToReleaseAnAmount_When_TryingToReleaseThatAmount_Then_CorrectlyReleasesThatAmount() {

        // Given: Coins To Release An Amount
        final CoinsDepositImpl coinsDepositImpl = new CoinsDepositImpl();
        Whitebox.setInternalState(coinsDepositImpl, "coins", coins);

        // When: Trying To Release That Amount
        final Optional<List<Coin>> optCoins = coinsDepositImpl.tryToReleaseAmount(
            Coin.TWO_EUROS.getValueInCents() + Coin.ONE_EURO.getValueInCents()
        );

        // Then: Correctly Releases That Amount
        assertThat(optCoins, is(Optional.of(Arrays.asList(Coin.TWO_EUROS, Coin.ONE_EURO))));
        assertThat(
            Whitebox.getInternalState(coinsDepositImpl, "coins"),
            is(Collections.singletonList(Coin.FIFTY_CENTS))
        );
    }

    @Test
    public void Given_NoCoinsToReleaseAnAmount_When_TryingToReleaseThatAmount_Then_CorrectlyDontReleasesThatAmount() {

        // Given: No Coins To Release An Amount
        final CoinsDepositImpl coinsDepositImpl = new CoinsDepositImpl();
        Whitebox.setInternalState(coinsDepositImpl, "coins", coins);

        // When: Trying To Release That Amount
        final Optional<List<Coin>> optCoins = coinsDepositImpl.tryToReleaseAmount(Coin.TEN_CENTS.getValueInCents());

        // Then: Correctly Don't Releases That Amount
        assertThat(optCoins, is(Optional.empty()));
        assertThat(
            Whitebox.getInternalState(coinsDepositImpl, "coins"),
            is(coins)
        );
    }

    @Test
    public void Given_AnySetOfCoins_When_SettingDepositedCoins_Then_CorrectlyReplacesSetOfCoins() {

        // Given: Not Empty Deposit
        final CoinsDepositImpl coinsDepositImpl = new CoinsDepositImpl();
        Whitebox.setInternalState(coinsDepositImpl, "coins", coins);

        // When: Setting Deposited Coins
        coinsDepositImpl.setCoins(Arrays.asList(Coin.TEN_CENTS, Coin.TEN_CENTS));

        // Then: Correctly Replaces Set Of Coins
        assertThat(
            Whitebox.getInternalState(coinsDepositImpl, "coins"),
            is(Arrays.asList(Coin.TEN_CENTS, Coin.TEN_CENTS))
        );
    }
}
