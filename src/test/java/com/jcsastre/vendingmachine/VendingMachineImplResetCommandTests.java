package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class VendingMachineImplResetCommandTests {

    @Mock
    private InventorizedDeposit<Coin> coinsDeposit;

    @Mock
    private InventorizedDeposit<Product> productsDeposit;

    @Spy
    private CoinsChangeCalculator coinsChangeCalculator;

    private Map<Coin, Integer> countByCoin;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        countByCoin = new HashMap<>();
        countByCoin.put(Coin.ONE_EURO, 1);
        countByCoin.put(Coin.FIFTY_CENTS, 2);
    }

    @Test
    public void shouldCorrectlyReset()
        throws NoProductStockException, ProductAlreadySelected, NoChangeException, InvalidStateException, TypeLimitExceededException {

        // Given
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl, "currentBalanceInCents", 100);
        Whitebox.setInternalState(vendingMachineImpl, "currentProduct", Product.SPRITE);
        when(coinsDeposit.getMaxCapacityPerEachType()).thenReturn(10);
        when(productsDeposit.getMaxCapacityPerEachType()).thenReturn(10);

        // When
        vendingMachineImpl.reset();

        // Then
        //TODO: Try alternative approach
        Mockito.verify(coinsDeposit).empty();
        Mockito.verify(coinsDeposit).insert(Coin.FIVE_CENTS, 5);
        Mockito.verify(coinsDeposit).insert(Coin.TEN_CENTS, 5);
        Mockito.verify(coinsDeposit).insert(Coin.TWENTY_CENTS, 5);
        Mockito.verify(coinsDeposit).insert(Coin.FIFTY_CENTS, 5);
        Mockito.verify(coinsDeposit).insert(Coin.ONE_EURO, 5);
        Mockito.verify(coinsDeposit).insert(Coin.TWO_EUROS, 5);
        Mockito.verify(productsDeposit).empty();
        Mockito.verify(productsDeposit).insert(Product.COKE, 10);
        Mockito.verify(productsDeposit).insert(Product.SPRITE, 10);
        Mockito.verify(productsDeposit).insert(Product.WATER, 10);
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }
}
