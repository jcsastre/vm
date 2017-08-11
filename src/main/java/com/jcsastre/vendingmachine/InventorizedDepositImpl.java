package com.jcsastre.vendingmachine;

import com.jcsastre.vendingmachine.exception.TypeLimitExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventorizedDepositImpl<T> implements InventorizedDeposit<T> {

    private Map<T, Integer> countByType = new HashMap<>();

    private Integer maxCapacityPerEachType;

    public InventorizedDepositImpl(Integer maxCapacityPerEachType) {

        this.maxCapacityPerEachType = maxCapacityPerEachType;
    }

    @Override
    public void insert(
        T type,
        Integer count
    ) throws TypeLimitExceededException {

        final Integer currentCount = getCountByType(type);
        Integer newCount = currentCount + count;

        if (newCount > maxCapacityPerEachType)
            throw new TypeLimitExceededException();

        countByType.put(type, newCount);
    }

    @Override
    public boolean hasType(T type) {

        return getCountByType(type) > 0;
    }

    @Override
    public Optional<T> tryToRelease(T type) {

        final Integer currentCount = getCountByType(type);

        if (currentCount > 0) {
            countByType.put(type, currentCount - 1);
            return Optional.of(type);
        }

        return Optional.empty();
    }

    @Override
    public void empty() {

        countByType.clear();
    }

    @Override
    public Integer getCountByType(T type) {

        final Integer count = countByType.get(type);
        return count!=null ? count : 0;
    }

    @Override
    public Map<T, Integer> getCountsForAllTypes() {

        return countByType;
    }

    @Override
    public Integer getMaxCapacityPerEachType() {

        return this.maxCapacityPerEachType;
    }
}
