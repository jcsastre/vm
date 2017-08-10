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

public class VendingMachineImplAtStateProductSelectedTests {

    @Mock
    private CoinsDeposit coinsDeposit;

    @Mock
    private ProductsDeposit productsDeposit;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    // insertCoin tests

    @Test // 2.1.1
    public void Given_BalanceNotEnough_When_InsertingCoinButNewBalanceIsStillNotEnough_CorrectlyUpdateBalance()
        throws NoChangeException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Product.COKE.getPriceInCents()-Coin.ONE_EURO.getValueInCents()
        );

        // When: Inserting Coin But New Balance Is Still Not Enough
        vendingMachineImpl.insertCoin(Coin.TEN_CENTS);

        // Then: Correctly Update Selected Product
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(
            vendingMachineImpl.readBalanceInCentsIndicator(),
            is(Product.COKE.getPriceInCents()-Coin.ONE_EURO.getValueInCents()+Coin.TEN_CENTS.getValueInCents())
        );
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
    }

    @Test // 2.1.2
    public void Given_BalanceNotEnough_When_InsertingCoinNewBalanceBecomesExactlyTheProductPrice_Then_CorrectlyReleaseProduct()
        throws NoChangeException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Product.COKE.getPriceInCents()-Coin.ONE_EURO.getValueInCents()
        );

        // When: Inserting Coin New Balance Becomes Exactly The ProductPrice
        vendingMachineImpl.insertCoin(Coin.ONE_EURO);

        // Then: Correctly Release Product
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
    }

    @Test // 2.1.3
    public void Given_BalanceNotEnough_When_InsertingCoinNewBalanceBecomesGreaterThanProductPriceButNoChange_Then_CorrectlyThrowNoChangeException()
        throws NoChangeException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Coin.FIFTY_CENTS.getValueInCents()
        );
        when(
            coinsDeposit.tryToReleaseAmount(
                Coin.FIFTY_CENTS.getValueInCents()+Coin.TWO_EUROS.getValueInCents()-Product.COKE.getPriceInCents()
            )
        )
            .thenReturn(Optional.empty());

        // When: Inserting Coin New Balance Becomes Greater Than Product Price But No Change
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Throw NoStockException
        then(caughtException()).isInstanceOf(NoChangeException.class);
        assertThat(
            vendingMachineImpl.readBalanceInCentsIndicator(),
            is(Product.COKE.getPriceInCents()-Coin.ONE_EURO.getValueInCents())
        );
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.TWO_EUROS))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test // 2.1.4
    public void Given_BalanceNotEnough_When_InsertingCoinAndNewBalanceBecomesGreaterThanProductPriceAndChange_Then_CorrectlyReleaseProduct()
        throws NoChangeException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Coin.FIFTY_CENTS.getValueInCents()
        );
        when(
            coinsDeposit.tryToReleaseAmount(
                Coin.FIFTY_CENTS.getValueInCents()+Coin.TWO_EUROS.getValueInCents()-Product.COKE.getPriceInCents()
            )
        )
            .thenReturn(Optional.of(Collections.singletonList(Coin.ONE_EURO)));

        // When: Inserting Coin And New Balance Becomes Greater Than Product Price And Change
        vendingMachineImpl.insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Release Product
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.ONE_EURO))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
    }
}
