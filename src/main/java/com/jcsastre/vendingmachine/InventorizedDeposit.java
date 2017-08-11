package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.TypeLimitExceededException;

import java.util.Map;
import java.util.Optional;

//TODO: javadoc
public interface InventorizedDeposit<T> {

    void insert(
        T type,
        Integer count
    ) throws TypeLimitExceededException;

    boolean hasType(T type);

    Optional<T> tryToRelease(T type);

    void empty();

    Integer getCountByType(T type);

    Map<T, Integer> getCountsForAllTypes();

    Integer getMaxCapacityByType(T type);

    void normalizeToMaxCapacityForEachType();

    void nomalizeToHalfCapacityForEachType();
}
