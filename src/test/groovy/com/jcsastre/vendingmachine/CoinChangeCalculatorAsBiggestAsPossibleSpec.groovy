package com.jcsastre.vendingmachine

import spock.lang.Specification


class CoinChangeCalculatorAsBiggestAsPossibleSpec extends Specification {

    def "Should return correct change"(int amountInCents, Optional<List<Coin>> change) {

        given: "a list of available coins"

        def availableCoins = [Coin.TWO_EUROS, Coin.FIFTY_CENTS, Coin.ONE_EURO]

        expect: "return correct change"

        CoinsChangeCalculator.coinChangeCalculatorAsBiggestAsPossible.calculate(availableCoins, amountInCents) == change

        where: "amount in cents is"

        amountInCents                                                         || change
        Coin.TWO_EUROS.getValueInCents() + Coin.FIFTY_CENTS.getValueInCents() || [Coin.TWO_EUROS, Coin.FIFTY_CENTS]
        Coin.TWO_EUROS.getValueInCents() + Coin.TWO_EUROS.getValueInCents()   || Optional.empty()
    }
}
