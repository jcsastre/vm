package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.TypeLimitExceededException;
import com.jcsastre.vendingmachine.exception.*;

import java.util.*;

/**
 * <p>Class implementing the {@link VendingMachine} interface.</p>
 *
 * <p>Is based in {@link CoinsDeposit} to manage the deposited coins, and in
 * {@link ProductsDeposit} to manage the stock of products.</p>
 *
 * @author Juan Carlos Sastre
 */
public class VendingMachineImpl implements VendingMachine {

    private InventorizedDeposit<Coin> coinsDeposit;
    private InventorizedDeposit<Product> productsDeposit;
    private CoinsChangeCalculator coinsChangeCalculator;

    private Product currentProduct;
    private int currentBalanceInCents;

    // Why not use Optional<Product> or Optional<List<Coin>>?
    // See discussion at https://stackoverflow.com/questions/23454952/uses-for-optional
    private Product productAtTakeoutPort;
    private List<Coin> coinsAtRepaymentPort;

    public VendingMachineImpl(
        InventorizedDeposit<Coin> coinsDeposit,
        InventorizedDeposit<Product> productsDeposit,
        CoinsChangeCalculator coinsChangeCalculator
    )  {

        this.coinsDeposit = coinsDeposit;
        this.productsDeposit = productsDeposit;
        this.coinsChangeCalculator = coinsChangeCalculator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertCoin(Coin coin) throws NoChangeException, NoProductStockException, DepositCoinOverflowException {

        try {
            coinsDeposit.insert(coin, 1);
        } catch (TypeLimitExceededException e) {
            throw new DepositCoinOverflowException();
        }

        currentBalanceInCents += coin.getValueInCents();

        if (currentProduct != null) {

            if (currentBalanceInCents >= currentProduct.getPriceInCents()) {

                try {

                    tryToReleaseProductAndReturnChangeIfRequired();

                } catch (NoChangeException e) {

                    currentBalanceInCents = currentBalanceInCents - coin.getValueInCents();
                    coinsAtRepaymentPort = Collections.singletonList(coin);

                    throw e;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectProduct(Product product) throws NoProductStockException, NoChangeException, ProductAlreadySelected {

        if (currentProduct != null)
            throw new ProductAlreadySelected();

        if (productsDeposit.hasType(product)) {

            currentProduct = product;

            if (currentBalanceInCents >= currentProduct.getPriceInCents()) {
                tryToReleaseProductAndReturnChangeIfRequired();
            }
        } else {
            throw new NoProductStockException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() throws InvalidStateException {

        if (currentBalanceInCents > 0) {

            final Optional<List<Coin>> optChange = tryToReleaseAmount(currentBalanceInCents);
            if (!optChange.isPresent()) {
                throw new InvalidStateException();
            }

            coinsAtRepaymentPort = optChange.get();
            currentBalanceInCents = 0;

        }

        currentProduct = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws InvalidStateException {

        // For each coin type, normalize to half the max capacity
        coinsDeposit.empty();
        final Integer maxCapacityPerEachCoinType = coinsDeposit.getMaxCapacityPerEachType();
        final int halfCapacityPerEachCoinType = maxCapacityPerEachCoinType / 2;
        for (Coin coin : Coin.values()) {
            try {
                coinsDeposit.insert(coin, halfCapacityPerEachCoinType);
            } catch (TypeLimitExceededException e) {
                throw new InvalidStateException();
            }
        }

        // For each product type, normalize to half the max capacity
        productsDeposit.empty();
        final Integer maxCapacityPerEachProductType = productsDeposit.getMaxCapacityPerEachType();
        for (Product product : Product.values()) {
            try {
                productsDeposit.insert(product, maxCapacityPerEachProductType);
            } catch (TypeLimitExceededException e) {
                throw new InvalidStateException();
            }
        }

        currentBalanceInCents = 0;
        currentProduct = null;

        coinsAtRepaymentPort = null;
        productAtTakeoutPort = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> collectProductAtTakeoutPort() {

        Optional<Product> optProduct = Optional.empty();

        if (productAtTakeoutPort != null) {
            optProduct = Optional.of(productAtTakeoutPort);
            productAtTakeoutPort = null;
        }

        return optProduct;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<List<Coin>> collectCoinsAtRepaymentPort() {

        Optional<List<Coin>> optCoins = Optional.empty();

        if (coinsAtRepaymentPort != null) {
            optCoins = Optional.of(coinsAtRepaymentPort);
            coinsAtRepaymentPort = null;
        }

        return optCoins;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer readBalanceInCentsIndicator() {
        return currentBalanceInCents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> readSelectedProductIndicator() {

        return
            Optional.ofNullable(currentProduct);
    }

    private void tryToReleaseProductAndReturnChangeIfRequired() throws NoChangeException, NoProductStockException {

        final Optional<Product> optProduct = productsDeposit.tryToRelease(currentProduct);
        if (!optProduct.isPresent())
            throw new NoProductStockException();

        Integer amountToReturnInCents = currentBalanceInCents - currentProduct.getPriceInCents();
        if (amountToReturnInCents == 0) {

            productAtTakeoutPort = currentProduct;
            currentBalanceInCents = 0;
            currentProduct = null;

        } else if (amountToReturnInCents > 0) {

            final Optional<List<Coin>> optChange = tryToReleaseAmount(amountToReturnInCents);
            if (!optChange.isPresent())
                throw new NoChangeException();

            coinsAtRepaymentPort = optChange.get();
            productAtTakeoutPort = currentProduct;
            currentBalanceInCents = 0;
            currentProduct = null;
        }
    }

    private Optional<List<Coin>> tryToReleaseAmount(Integer amountToProvideInCents) {

        // TODO: Try using a declarative approach to see if readability is improved

        List<Coin> availableCoins = new ArrayList<>();
        final Map<Coin, Integer> countsByCoin = coinsDeposit.getCountsForAllTypes();
        for (Map.Entry<Coin, Integer> coinIntegerEntry : countsByCoin.entrySet()) {
            final Coin coin = coinIntegerEntry.getKey();
            final Integer count = coinIntegerEntry.getValue();
            for (int i=0; i<count; i++) {
                availableCoins.add(coin);
            }
        }

        final Optional<List<Coin>> optChange =
            coinsChangeCalculator.calculate(
                availableCoins,
                amountToProvideInCents
            );

        if (optChange.isPresent()) {
            final List<Coin> coinsRequiredForChange = optChange.get();
            for (Coin coin : coinsRequiredForChange) {
                final Optional<Coin> optReleasedCoin = coinsDeposit.tryToRelease(coin);
                if (!optReleasedCoin.isPresent())
                    return Optional.empty();
            }
            return Optional.of(coinsRequiredForChange);
        }

        return Optional.empty();
    }
}
