package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.InvalidStateException;
import com.jcsastre.vendingmachine.exception.NoChangeException;
import com.jcsastre.vendingmachine.exception.NoStockException;

import java.util.List;
import java.util.Optional;

public interface VendingMachine {

    void insertCoin(Coin coin) throws NoChangeException;
    void selectProduct(Product product) throws NoStockException, NoChangeException;
    void cancel() throws InvalidStateException;

    void reset();

    Optional<Product> collectProductAtTakeoutPort();
    Optional<List<Coin>> collectCoinsAtRepaymentPort();

    Integer readBalanceInCentsIndicator();
    Optional<Product> readSelectedProductIndicator();
}
