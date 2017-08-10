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

    @Mock
    private CoinsDeposit coinsDeposit;

    @Mock
    private ProductsDeposit productsDeposit;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    // insertCoin tests

    @Test
    public void Given_NoBalance_WhenInsertingCoin_Then_CorrectlyUpdateBalance()
        throws NoChangeException {

        // Given: Product Not Selected And NoBalance
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);

        // When: Inserting Coin
        vendingMachineImpl.insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Update Balance
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(Coin.TWO_EUROS.getValueInCents()));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test
    public void Given_Balance_WhenInsertingCoin_Then_CorrectlyUpdateBalance()
        throws NoChangeException {

        // Given: Product Not Selected And Balance
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents",100);

        // When: Inserting Coin
        vendingMachineImpl.insertCoin(Coin.TWO_EUROS);

        // Then: Correctly Update Balance
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(300));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    // selectProduct tests

    @Test // 1.2.1
    public void Given_NoStockOfAProduct_When_SelectingThatProduct_Then_CorrectlyThrowNoStockException()
        throws NoChangeException, NoStockException {

        // Given: No Stock Of A Product
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        when(productsDeposit.checkThereIsProductStock(Product.COKE)).thenReturn(false);

        // When: Selecting That Product
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).selectProduct(Product.COKE);

        // Then: Correctly Throw NoStockException
        then(caughtException()).isInstanceOf(NoStockException.class);
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test // 1.2.2
    public void Given_NoBalanceEnoughForAProduct_When_SelectingThatProduct_Then_CorrectlyUpdateSelectedProduct()
        throws NoChangeException, NoStockException {

        // Given: No Balance Enough For A Product
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        when(productsDeposit.checkThereIsProductStock(Product.COKE)).thenReturn(true);

        // When: Selecting That Product
        vendingMachineImpl.selectProduct(Product.COKE);

        // Then: Correctly Update Selected Product
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
    }

    @Test // 1.2.3
    public void Given_ExactBalanceForAProduct_When_SelectingThatProduct_Then_CorrectlyReleaseThatProduct()
        throws NoChangeException, NoStockException {

        // Given: Exact Balance For A Product
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        when(productsDeposit.checkThereIsProductStock(Product.COKE)).thenReturn(true);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents",Product.COKE.getPriceInCents());

        // When: Selecting That Product
        vendingMachineImpl.selectProduct(Product.COKE);

        // Then: Correctly Release That Product
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
    }

    @Test // 1.2.4
    public void Given_MoreThanRequiredBalanceForAProductButNoChangeAvailable_When_SelectingThatProduct_Then_CorrectlyThrowNoChangeExceptionReturnCoin()
        throws NoChangeException, NoStockException {

        // Given: More Than Required Balance For A Product But No ChangeAvailable
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        when(productsDeposit.checkThereIsProductStock(Product.COKE)).thenReturn(true);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents", 200);
        when(coinsDeposit.tryToReleaseAmount(50)).thenReturn(Optional.empty());

        // When: Selecting That Product
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).selectProduct(Product.COKE);

        // Then: Correctly Throw NoStockException
        then(caughtException()).isInstanceOf(NoChangeException.class);
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(200));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test // 1.2.5
    public void Given_MoreThanRequiredBalanceForAProduct_When_SelectingThatProduct_Then_CorrectlyReleaseTheProduct()
        throws NoChangeException, NoStockException {

        // Given: More Than Required Balance For A Product
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        when(productsDeposit.checkThereIsProductStock(Product.COKE)).thenReturn(true);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents", 200);
        when(coinsDeposit.tryToReleaseAmount(50)).thenReturn(Optional.of(Collections.singletonList(Coin.FIFTY_CENTS)));

        // When: Selecting That Product
        vendingMachineImpl.selectProduct(Product.COKE);

        // Then: Correctly Release The Product
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.FIFTY_CENTS))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
    }

    // cancel tests

    @Test // 1.3.1
    public void Given_NoBalance_When_Canceling_Then_CorrectlyDoNothing() throws InvalidStateException {

        // Given: No Balance
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);

        // When: Canceling
        vendingMachineImpl.cancel();

        // Then: Correctly Do Nothing
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test // 1.3.2
    public void Given_Balance_When_Canceling_Then_CorrectlyReleaseBalance() throws InvalidStateException {

        // Given: Balance
        final VendingMachineImpl vendingMachineImpl = new VendingMachineImpl(coinsDeposit, productsDeposit);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents", 50);
        when(coinsDeposit.tryToReleaseAmount(50)).thenReturn(Optional.of(Collections.singletonList(Coin.FIFTY_CENTS)));

        // When: Canceling
        vendingMachineImpl.cancel();

        // Then: Correctly Release Balance
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.FIFTY_CENTS))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }
}
