package com.jcsastre.vendingmachine;

import com.google.common.collect.ImmutableMap;
import com.jcsastre.vendingmachine.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class VendingMachineImplAtStateProductSelectedTest {

    @Spy
    private InventorizedDeposit<Coin> coinsDeposit;

    @Mock
    private InventorizedDeposit<Product> productsDeposit;

    @Mock
    private CoinsChangeCalculator coinsChangeCalculator;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    // insertCoin tests

    @Test // 2.1.1
    public void Given_BalanceNotEnough_When_InsertingCoinButNewBalanceIsStillNotEnough_CorrectlyUpdateBalance()
        throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
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
        throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Product.COKE.getPriceInCents()-Coin.ONE_EURO.getValueInCents()
        );
        when(productsDeposit.tryToRelease(Product.COKE)).thenReturn(Optional.of(Product.COKE));

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
        throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Coin.FIFTY_CENTS.getValueInCents()
        );
        when(productsDeposit.tryToRelease(Product.COKE)).thenReturn(Optional.of(Product.COKE));

        // When: Inserting Coin New Balance Becomes Greater Than Product Price But No Change
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Throw NoProductStockException
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
        throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Coin.FIFTY_CENTS.getValueInCents()
        );
        when(productsDeposit.tryToRelease(Product.COKE)).thenReturn(Optional.of(Product.COKE));
        when(coinsDeposit.getCountsForAllTypes()).thenReturn(ImmutableMap.of(Coin.ONE_EURO, 1));
        when(coinsChangeCalculator.calculate(Arrays.asList(Coin.ONE_EURO), 100))
            .thenReturn(Optional.of(Arrays.asList(Coin.ONE_EURO)));
        when(coinsDeposit.tryToRelease(Coin.ONE_EURO)).thenReturn(Optional.of(Coin.ONE_EURO));

        // When: Inserting Coin And New Balance Becomes Greater Than Product Price And Change
        vendingMachineImpl.insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Release Product
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.ONE_EURO))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
    }

    // selectProduct tests

    @Test // 2.2.1
    public void Given_BalanceNotEnough_When_SelectingAnyProduct_Then_CorrectlyThrowProductAlreadySelected()
        throws NoChangeException, NoProductStockException, ProductAlreadySelected {

        // Given: Balance Not Enough
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(
            vendingMachineImpl,
            "currentBalanceInCents",
            Coin.FIFTY_CENTS.getValueInCents()
        );

        // When: Selecting Any Product
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).selectProduct(Product.SPRITE);

        // Then: Correctly Throw Product Already Selected
        then(caughtException()).isInstanceOf(ProductAlreadySelected.class);
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(Coin.FIFTY_CENTS.getValueInCents()));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    // cancel tests

    @Test // 2.3.1
    public void Given_NoBalance_When_Canceling_Then_CorrectlyResetTheCurrentSelectedProduct() throws InvalidStateException {

        // Given: No Balance
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);

        // When: Canceling
        vendingMachineImpl.cancel();

        // Then: Correctly Reset The Current Selected Product
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test // 2.3.2
    public void Given_Balance_When_Canceling_Then_CorrectlyReleaseBalanceAndResetTheCurrentSelectedProduct() throws InvalidStateException {

        // Given: Balance
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentProduct", Product.COKE);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents", 50);
        when(coinsDeposit.getCountsForAllTypes()).thenReturn(ImmutableMap.of(Coin.FIFTY_CENTS, 1));
        when(coinsChangeCalculator.calculate(Arrays.asList(Coin.FIFTY_CENTS), 50))
            .thenReturn(Optional.of(Arrays.asList(Coin.FIFTY_CENTS)));
        when(coinsDeposit.tryToRelease(Coin.FIFTY_CENTS)).thenReturn(Optional.of(Coin.FIFTY_CENTS));

        // When: Canceling
        vendingMachineImpl.cancel();

        // Then: Correctly Release Balance
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.FIFTY_CENTS))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }
}
