package com.jcsastre.vendingmachine;

import java.util.Optional;

public interface ProductsDeposit {

    void addProduct(Product product);
    boolean checkThereIsProductStock(Product product);

    Optional<Product> tryToReleaseProduct();
}
