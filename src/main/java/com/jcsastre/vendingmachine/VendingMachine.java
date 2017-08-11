package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.*;

import java.util.List;
import java.util.Optional;

/**
 * <p>Interface representing the commands a vending machine implementation has to support.</p>
 *
 * <p>This interface is based on a vending machine with these components:</p>
 *
 * <ul>
 *     <li>A coin slot to insert coins.</li>
 *     <li>A set of buttons to select a product.</li>
 *     <li>A cancel button.</li>
 *     <li>A backdoor where a supplier can reset the machine state.</li>
 *     <li>A takeout port to collect the released product.</li>
 *     <li>A repayment port to collect the released change.</li>
 *     <li>An indicator informing the current balance.</li>
 *     <li>An indicator informing the current selected product.</li>
 * </ul>
 */
public interface VendingMachine {

    /**
     * <p>Command representing a Customer inserting a coin.</p>
     *
     * @param coin the {@link Coin] inserted.
     * @throws NoChangeException if a product is already selected, but is not
     *         possible to return change with the new balance produced by the
     *         inserted coin.
     */
    void insertCoin(Coin coin) throws NoChangeException, NoProductStockException, DepositCoinOverflowException;

    /**
     * <p>Command representing a Customer selecting a product.</p>
     *
     * @param product the {@link Product} selected.
     * @throws NoProductStockException if no stock available.
     * @throws NoChangeException if it is not possible to return change with the
     *         current balance.
     * @throws ProductAlreadySelected if a product is already selected.
     */
    void selectProduct(Product product) throws NoProductStockException, NoChangeException, ProductAlreadySelected;

    /**
     * <p>Command representing a Customer pressing the cancel button.</p>
     *
     * <p>If a product is selected, then should cancel it. If there is balance, then
     * should cancel it and return coins at the repayment port.</p>
     *
     * @throws InvalidStateException if there is balance and is not possible to return it.
     */
    void cancel() throws InvalidStateException;

    /**
     * <p>Represents a Supplier resetting the vending the machine:</p>
     *
     * <ul>
     *     <li>Normalizes the coins deposit.</li>
     *     <li>Refills products stock at maximum capacity.</li>
     *     <li>If there is balance then is set to zero.</li>
     *     <li>If there is a selected product then is set to none.</li>
     *     <li>Removes any product of the takeout port.</li>
     *     <li>Removes any coin of the repayment port.</li>
     * </ul>
     */
    void reset() throws InvalidStateException;

    /**
     * <p>Command representing a Customer opening the takeout port to collect a {@link Product}</p>
     *
     * @return The {@link Product} or empty if no product has been released.
     */
    Optional<Product> collectProductAtTakeoutPort();

    /**
     * <p>Command representing a Customer opening the repayment port to collect the change as
     * a list of {@link Coin}.</p>
     *
     * @return The list of {@link Coin} or empty if no change has been released.
     */
    Optional<List<Coin>> collectCoinsAtRepaymentPort();

    /**
     * <p>Command representing a Customer reading the balance indicator.</p>
     *
     * @return A value representing the current balance.
     */
    Integer readBalanceInCentsIndicator();

    /**
     * <p>Command representing a Customer reading the selected {@link Product}.</p>
     *
     * @return A {@link Product} representing the current selected product or empty.
     */
    Optional<Product> readSelectedProductIndicator();

    public static final int NORMALIZED_COUNT_PER_COIN_TYPE = 5;
}
