package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.coinsdeposit.CoinsDeposit;
import com.jcsastre.vendingmachine.exception.InvalidStateException;
import com.jcsastre.vendingmachine.exception.NoChangeException;
import com.jcsastre.vendingmachine.exception.NoStockException;
import com.jcsastre.vendingmachine.exception.ProductAlreadySelected;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class VendingMachineImplResetCommandTests {

    @Mock
    private CoinsDeposit coinsDeposit;

    @Mock
    private ProductsDeposit productsDeposit;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCorrectlyReset() throws NoStockException, ProductAlreadySelected, NoChangeException {

        // Given
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl, "currentBalanceInCents", 100);
        Whitebox.setInternalState(vendingMachineImpl, "currentProduct", Product.SPRITE);

        // When
        vendingMachineImpl.reset();

        // Then
        // Normalizes the coins deposit.
        // Refills products stock at maximum capacity.
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }
}
