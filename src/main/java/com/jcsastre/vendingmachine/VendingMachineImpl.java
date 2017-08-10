package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.InvalidStateException;
import com.jcsastre.vendingmachine.exception.NoChangeException;
import com.jcsastre.vendingmachine.exception.NoStockException;

import java.util.List;
import java.util.Optional;

public class VendingMachineImpl implements VendingMachine {

    private Product currentProduct;


    @Override
    public void insertCoin(Coin coin) throws NoChangeException {

    }

    @Override
    public void selectProduct(Product product) throws NoStockException, NoChangeException {

    }

    @Override
    public void cancel() throws InvalidStateException {

    }

    @Override
    public void reset() {

    }

    @Override
    public Optional<Product> collectProductAtTakeoutPort() {
        return null;
    }

    @Override
    public Optional<List<Coin>> collectCoinsAtRepaymentPort() {
        return null;
    }

    @Override
    public Integer readBalanceInCentsIndicator() {
        return null;
    }

    @Override
    public Optional<Product> readSelectedProductIndicator() {
        return null;
    }
}
