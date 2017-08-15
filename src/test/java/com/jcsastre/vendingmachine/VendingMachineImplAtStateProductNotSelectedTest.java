package com.jcsastre.vendingmachine;

import com.google.common.collect.ImmutableMap;
import com.jcsastre.vendingmachine.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * <p>This class contains tests for the expected behaviour of {@link VendingMachineImpl}
 * when no {@link Product} is selected.</p>
 *
 * @author Juan Carlos Sastre
 */
public class VendingMachineImplAtStateProductNotSelectedTest {

    @Mock
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

    @Test
    public void Given_NoBalance_WhenInsertingCoin_Then_CorrectlyUpdateBalance()
        throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        // Given: Product Not Selected And NoBalance
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);

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
        throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        // Given: Product Not Selected And Balance
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
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

    @Test
    public void Given_NoStockOfAProduct_When_SelectingThatProduct_Then_CorrectlyThrowNoStockException()
        throws NoChangeException, NoProductStockException, ProductAlreadySelected {

        // Given: No Stock Of A Product
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        when(productsDeposit.hasType(Product.COKE)).thenReturn(false);

        // When: Selecting That Product
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).selectProduct(Product.COKE);

        // Then: Correctly Throw NoProductStockException
        then(caughtException()).isInstanceOf(NoProductStockException.class);
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test
    public void Given_NoBalanceEnoughForAProduct_When_SelectingThatProduct_Then_CorrectlyUpdateSelectedProduct()
        throws NoChangeException, NoProductStockException, ProductAlreadySelected {

        // Given: No Balance Enough For A Product
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        when(productsDeposit.hasType(Product.COKE)).thenReturn(true);

        // When: Selecting That Product
        vendingMachineImpl.selectProduct(Product.COKE);

        // Then: Correctly Update Selected Product
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
    }

    @Test
    public void Given_ExactBalanceForAProduct_When_SelectingThatProduct_Then_CorrectlyReleaseThatProduct()
        throws NoChangeException, NoProductStockException, ProductAlreadySelected {

        // Given: Exact Balance For A Product
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents",Product.COKE.getPriceInCents());
        when(productsDeposit.hasType(Product.COKE)).thenReturn(true);
        when(productsDeposit.tryToRelease(Product.COKE)).thenReturn(Optional.of(Product.COKE));

        // When: Selecting That Product
        vendingMachineImpl.selectProduct(Product.COKE);

        // Then: Correctly Release That Product
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
    }

    @Test
    public void Given_MoreThanRequiredBalanceForAProductButNoChangeAvailable_When_SelectingThatProduct_Then_CorrectlyThrowNoChangeExceptionReturnCoin()
        throws NoChangeException, NoProductStockException, ProductAlreadySelected {

        // Given: More Than Required Balance For A Product But No ChangeAvailable
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents", 200);
        when(productsDeposit.hasType(Product.COKE)).thenReturn(true);
        when(productsDeposit.tryToRelease(Product.COKE)).thenReturn(Optional.of(Product.COKE));

        // When: Selecting That Product
        com.googlecode.catchexception.apis.BDDCatchException.when(vendingMachineImpl).selectProduct(Product.COKE);

        // Then: Correctly Throw NoProductStockException
        then(caughtException()).isInstanceOf(NoChangeException.class);
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(200));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.of(Product.COKE)));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test
    public void Given_MoreThanRequiredBalanceForAProduct_When_SelectingThatProduct_Then_CorrectlyReleaseTheProduct()
        throws NoChangeException, NoProductStockException, ProductAlreadySelected {

        // TODO: try to use Fake approach because the required mocking is difficult to maintain

        // Given: More Than Required Balance For A Product
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
        Whitebox.setInternalState(vendingMachineImpl,"currentBalanceInCents", 200);
        when(productsDeposit.hasType(Product.COKE)).thenReturn(true);
        when(productsDeposit.tryToRelease(Product.COKE)).thenReturn(Optional.of(Product.COKE));
        when(coinsDeposit.getCountsForAllTypes()).thenReturn(ImmutableMap.of(Coin.FIFTY_CENTS, 1));
        when(coinsChangeCalculator.calculate(Arrays.asList(Coin.FIFTY_CENTS), 50))
            .thenReturn(Optional.of(Arrays.asList(Coin.FIFTY_CENTS)));
        when(coinsDeposit.tryToRelease(Coin.FIFTY_CENTS)).thenReturn(Optional.of(Coin.FIFTY_CENTS));

        // When: Selecting That Product
        vendingMachineImpl.selectProduct(Product.COKE);

        // Then: Correctly Release The Product
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.of(Collections.singletonList(Coin.FIFTY_CENTS))));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.of(Product.COKE)));
    }

    // cancel tests

    @Test
    public void Given_NoBalance_When_Canceling_Then_CorrectlyDoNothing() throws InvalidStateException {

        // Given: No Balance
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);

        // When: Canceling
        vendingMachineImpl.cancel();

        // Then: Correctly Do Nothing
        assertThat(vendingMachineImpl.readBalanceInCentsIndicator(), is(0));
        assertThat(vendingMachineImpl.readSelectedProductIndicator(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectCoinsAtRepaymentPort(), is(Optional.empty()));
        assertThat(vendingMachineImpl.collectProductAtTakeoutPort(), is(Optional.empty()));
    }

    @Test
    public void Given_Balance_When_Canceling_Then_CorrectlyReleaseBalance() throws InvalidStateException {

        // TODO: try to use Fake approach because the required mocking is difficult to maintain
        // Given: Balance
        final VendingMachineImpl vendingMachineImpl =
            new VendingMachineImpl(coinsDeposit, productsDeposit, coinsChangeCalculator);
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
