package com.jcsastre.vendingmachine

import spock.lang.Specification


class CoinChangeCalculatorAsBiggestAsPossibleSpec extends Specification {

    def "Should return correct change"(int amountInCents, Optional<List<Coin>> change) {

        given: "a list of available coins"

        def availableCoins = [Coin.TWO_EUROS, Coin.FIFTY_CENTS, Coin.ONE_EURO]

        expect: "return correct change"

        CoinsChangeCalculator.coinChangeCalculatorAsBiggestAsPossible.calculate(availableCoins, amountInCents) == change

        where: "amount in cents is"

        amountInCents || change
        20            || Optional.empty()
        50            || Optional.of([Coin.FIFTY_CENTS])
        150           || Optional.of([Coin.ONE_EURO, Coin.FIFTY_CENTS])
        250           || Optional.of([Coin.TWO_EUROS, Coin.FIFTY_CENTS])
        400           || Optional.empty()
    }
}
