package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.NoProductStockException;
import com.jcsastre.vendingmachine.exception.TypeLimitExceededException;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class InventorizedDepositImplTests {

    private Map<Coin, Integer> countByCoin;

    @Before
    public void setUp() {

        countByCoin = new HashMap<>();
        countByCoin.put(Coin.ONE_EURO, 1);
        countByCoin.put(Coin.FIFTY_CENTS, 2);
    }

    @Test
    public void insert_shouldInsert() throws TypeLimitExceededException {

        //TODO independize of getCountByType behaviour

        // Given:
        final InventorizedDepositImpl<Coin> inventorizedDepositImpl =
            new InventorizedDepositImpl<>(5);
        Whitebox.setInternalState(inventorizedDepositImpl, "countByType", countByCoin);

        // When
        inventorizedDepositImpl.insert(Coin.FIFTY_CENTS, 1);

        // Then
        assertThat(inventorizedDepositImpl.getCountByType(Coin.ONE_EURO), is(1));
        assertThat(inventorizedDepositImpl.getCountByType(Coin.FIFTY_CENTS), is(3));
    }

    @Test
    public void insert_shouldThrowTypeLimitExceededException() throws TypeLimitExceededException {

        //TODO independize of getCountByType behaviour

        // Given:
        final InventorizedDepositImpl<Coin> inventorizedDepositImpl =
            new InventorizedDepositImpl<>(5);
        Whitebox.setInternalState(inventorizedDepositImpl, "countByType", countByCoin);

        // When
        com.googlecode.catchexception.apis.BDDCatchException.when(
            inventorizedDepositImpl
        ).insert(Coin.FIFTY_CENTS, 10);

        // Then
        then(caughtException()).isInstanceOf(TypeLimitExceededException.class);
        assertThat(inventorizedDepositImpl.getCountByType(Coin.ONE_EURO), is(1));
        assertThat(inventorizedDepositImpl.getCountByType(Coin.FIFTY_CENTS), is(2));
    }

    @Test
    public void hasType_shouldReturnTrueWhenHasAType() {

        //TODO independize of getCountByType behaviour

        // Given:
        final InventorizedDepositImpl<Coin> inventorizedDepositImpl =
            new InventorizedDepositImpl<>(5);
        Whitebox.setInternalState(inventorizedDepositImpl, "countByType", countByCoin);

        // When
        final boolean hasType = inventorizedDepositImpl.hasType(Coin.ONE_EURO);

        // Then
        assertThat(hasType, is(true));
    }

    @Test
    public void hasType_shouldReturnFalseWhenDoesNotHaveAType() {

        //TODO independize of getCountByType behaviour

        // Given:
        final InventorizedDepositImpl<Coin> inventorizedDepositImpl =
            new InventorizedDepositImpl<>(5);
        Whitebox.setInternalState(inventorizedDepositImpl, "countByType", countByCoin);

        // When
        final boolean hasType = inventorizedDepositImpl.hasType(Coin.TWO_EUROS);

        // Then
        assertThat(hasType, is(false));
    }

    @Test
    public void tryToRelease_shouldReturnEmptyWhenTryingToReleaseATypeThatDoesNotHave() {

        // Given:
        final InventorizedDepositImpl<Coin> inventorizedDepositImpl =
            new InventorizedDepositImpl<>(5);
        Whitebox.setInternalState(inventorizedDepositImpl, "countByType", countByCoin);

        // When
        final Optional<Coin> optCoin = inventorizedDepositImpl.tryToRelease(Coin.TWO_EUROS);

        // Then
        assertThat(optCoin, is(Optional.empty()));
    }

    @Test
    public void tryToRelease_shouldReleaseATypeWhenTryingToReleaseATypeThatHas() {

        // Given:
        final InventorizedDepositImpl<Coin> inventorizedDepositImpl =
            new InventorizedDepositImpl<>(5);
        Whitebox.setInternalState(inventorizedDepositImpl, "countByType", countByCoin);

        // When
        final Optional<Coin> optCoin = inventorizedDepositImpl.tryToRelease(Coin.ONE_EURO);

        // Then
        assertThat(optCoin, is(Optional.of(Coin.ONE_EURO)));
        assertThat(inventorizedDepositImpl.getCountByType(Coin.ONE_EURO), is(0));
    }

    @Test
    public void shouldEmptyAllTypes() {
        assert false;
    }

    @Test
    public void shouldReturnCountOfAType() {
        assert false;
    }

    @Test
    public void shouldReturnTheCountOfAllTypes() {
        assert false;
    }

    @Test
    public void shouldReturnTheMaxCapacityPerEachType() {

        //When
        assert false;
    }

    @Test
    public void shouldNormalizeToMaxCapacityForEachType() {
        assert false;
    }

    @Test
    public void shouldNomalizeToHalfCapacityForEachType() {
        assert false;
    }
}
