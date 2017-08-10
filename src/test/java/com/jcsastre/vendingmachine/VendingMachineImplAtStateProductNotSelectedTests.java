package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.InvalidStateException;
import com.jcsastre.vendingmachine.exception.NoChangeException;
import com.jcsastre.vendingmachine.exception.NoStockException;
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

public class VendingMachineImplAtStateProductNotSelectedTests {

    @Test
    public void Given_ProductNotSelectedAndNoBalance_WhenInsertingCoin_Then_CorrectlyUpdateBalance()
        throws NoChangeException {

        // Given: Product Not Selected And NoBalance
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl();

        // When: Inserting Coin
        vendingMachineImpl.insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Update Balance
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(Coin.TWO_EUROS.getValueInCents()));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test
    public void Given_ProductNotSelectedAndBalance_WhenInsertingCoin_Then_CorrectlyUpdateBalance()
        throws NoChangeException {

        // Given: Product Not Selected And Balance
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl();
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents",100);

        // When: Inserting Coin
        vendingMachineImpl.insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Update Balance
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(300));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }
}
