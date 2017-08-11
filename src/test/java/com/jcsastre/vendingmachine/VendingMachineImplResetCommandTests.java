package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.NoChangeException;
import com.jcsastre.vendingmachine.exception.NoProductStockException;
import com.jcsastre.vendingmachine.exception.ProductAlreadySelected;
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

public class VendingMachineImplResetCommandTests {

    @Spy
    private InventorizedDeposit<Coin> coinsDeposit;

    @Mock
    private InventorizedDeposit<Product> productsDeposit;

    @Mock
    private CoinsChangeCalculator coinsChangeCalculator;

    private List<Coin> expectedListOfCoinsToBeSetOnCoinsDeposit;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        expectedListOfCoinsToBeSetOnCoinsDeposit = new ArrayList<>();
        Arrays.stream(Coin.values())
            .forEach((Coin coin) -> {
                for (int i=0; i<VendingMachine.NORMALIZED_COUNT_PER_COIN_TYPE; i++) {
                    expectedListOfCoinsToBeSetOnCoinsDeposit.add(coin);
                }
            });
    }

    @Test
    public void shouldCorrectlyReset() throws NoProductStockException, ProductAlreadySelected, NoChangeException {

        // Given
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl, "currentBalanceInCents", 100);
        Whitebox.setInternalState(vendingMachineImpl, "currentProduct", Product.SPRITE);

        // When
        vendingMachineImpl.reset();

        // Then
        Mockito.verify(coinsDeposit).nomalizeToHalfCapacityForEachType();
        Mockito.verify(productsDeposit).normalizeToMaxCapacityForEachType();
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }
}
