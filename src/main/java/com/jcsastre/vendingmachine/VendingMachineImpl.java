package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.coinsdeposit.CoinsDeposit;
import com.jcsastre.vendingmachine.exception.InvalidStateException;
import com.jcsastre.vendingmachine.exception.NoChangeException;
import com.jcsastre.vendingmachine.exception.NoStockException;
import com.jcsastre.vendingmachine.exception.ProductAlreadySelected;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>Class implementing the {@link VendingMachine} interface.</p>
 *
 * <p>Is based in {@link CoinsDeposit} to manage the deposited coins, and in
 * {@link ProductsDeposit} to manage the stock of products.</p>
 *
 * @author Juan Carlos Sastre
 */
public class VendingMachineImpl implements VendingMachine {

    private CoinsDeposit coinsDeposit;
    private ProductsDeposit productsDeposit;

    private Product currentProduct;
    private int currentBalanceInCents;

    // Why not use Optional<Product> or Optional<List<Coin>>?
    // See discussion at https://stackoverflow.com/questions/23454952/uses-for-optional
    private Product productAtTakeoutPort;
    private List<Coin> coinsAtRepaymentPort;

    public VendingMachineImpl(
        CoinsDeposit coinsDeposit,
        ProductsDeposit productsDeposit
    )  {

        this.coinsDeposit = coinsDeposit;
        this.productsDeposit = productsDeposit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertCoin(Coin coin) throws NoChangeException {

        coinsDeposit.insertCoin(coin);

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
    public void selectProduct(Product product) throws NoStockException, NoChangeException, ProductAlreadySelected {

        if (currentProduct != null)
            throw new ProductAlreadySelected();

        if (productsDeposit.checkThereIsProductStock(product)) {

            currentProduct = product;

            if (currentBalanceInCents >= currentProduct.getPriceInCents()) {
                tryToReleaseProductAndReturnChangeIfRequired();
            }
        } else {
            throw new NoStockException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() throws InvalidStateException {

        if (currentBalanceInCents > 0) {

            final Optional<List<Coin>> optChange = coinsDeposit.tryToReleaseAmount(currentBalanceInCents);
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
    public void reset() {
        //TODO
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

    private void tryToReleaseProductAndReturnChangeIfRequired() throws NoChangeException {

        Integer amountToReturnInCents = currentBalanceInCents - currentProduct.getPriceInCents();
        if (amountToReturnInCents == 0) {

            productAtTakeoutPort = currentProduct;
            currentBalanceInCents = 0;
            currentProduct = null;

        } else if (amountToReturnInCents > 0) {

            final Optional<List<Coin>> optChange = coinsDeposit.tryToReleaseAmount(amountToReturnInCents);
            if (!optChange.isPresent()) {
                throw new NoChangeException();
            }

            coinsAtRepaymentPort = optChange.get();
            productAtTakeoutPort = currentProduct;
            currentBalanceInCents = 0;
            currentProduct = null;
        }
    }
}
