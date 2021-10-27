package com.stevencarley.vendingdemo.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum Currency {
    PENNY(".01", false),
    NICKEL(".05", true),
    DIME(".1", true),
    QUARTER(".25", true),
    HALF_DOLLAR(".50", false),
    DOLLAR("1.00", false);

    private BigDecimal value;
    private boolean valid;

    Currency(String val, boolean valid) {
        this.value = new BigDecimal(val);
        this.valid = valid;
    }

    public static Currency getCurrency(Coin coin) {
        Currency currency = null;
        if (coin != null && coin.getValue() != null) {
            currency = Arrays.stream(Currency.values())
                    .filter(curr -> curr.value.compareTo(new BigDecimal(coin.getValue())) == 0)
                    .findFirst()
                    .orElse(null);
        }
        return currency;
    }
}
