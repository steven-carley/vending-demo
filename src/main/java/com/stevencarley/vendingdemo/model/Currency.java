package com.stevencarley.vendingdemo.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Coin specifications taken from https://www.usmint.gov/learn/coin-and-medal-programs/coin-specifications
 */
@Getter
public enum Currency {
    PENNY(".01", false, "2.500", "0.750"),
    NICKEL(".05", true, "5.000", "0.835"),
    DIME(".1", true, "2.268", "0.705"),
    QUARTER(".25", true, "5.670", "0.955"),
    HALF_DOLLAR(".50", false, "11.340", "1.205"),
    DOLLAR("1.00", false, "81", "1.043");

    private BigDecimal value;
    private boolean valid;
    private BigDecimal weight;
    private BigDecimal diameter;

    Currency(String val, boolean valid, String weight, String diameter) {
        this.value = new BigDecimal(val);
        this.valid = valid;
        this.weight = new BigDecimal(weight);
        this.diameter = new BigDecimal(diameter);
    }

    public static Currency getCurrency(Coin coin) {
        Currency currency = null;
        if (coin != null && coin.getDiameter() != null && coin.getWeight() != null) {
            currency = Arrays.stream(Currency.values())
                    .filter(curr -> curr.weight.compareTo(new BigDecimal(coin.getWeight())) == 0)
                    .filter(curr -> curr.diameter.compareTo(new BigDecimal(coin.getDiameter())) == 0)
                    .findAny()
                    .orElse(null);
        }
        return currency;
    }
}
