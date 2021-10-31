package com.stevencarley.vendingdemo.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * Coin specifications taken from https://www.usmint.gov/learn/coin-and-medal-programs/coin-specifications
 */
@Getter
@Slf4j
public enum Currency {
    PENNY(".01", false, "2.500", "0.750"),
    NICKEL(".05", true, "5.000", "0.835"),
    DIME(".1", true, "2.268", "0.705"),
    QUARTER(".25", true, "5.670", "0.955"),
    HALF_DOLLAR(".50", false, "11.340", "1.205"),
    DOLLAR("1.00", false, "8.1", "1.043"),
    UNKNOWN("0.0", false, "8", "8");

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
        Currency currency = UNKNOWN;
        if (coin != null && coin.getDiameter() != null && coin.getWeight() != null) {
            currency = Arrays.stream(Currency.values())
                    .filter(curr -> curr.weight.compareTo(safeConvert(coin.getWeight())) == 0)
                    .filter(curr -> curr.diameter.compareTo(safeConvert(coin.getDiameter())) == 0)
                    .findAny()
                    .orElse(null);
        }
        return currency;
    }

    private static BigDecimal safeConvert(String string) {
        try {
            return new BigDecimal(string);
        }
        catch (NumberFormatException e) {
            log.info("Exception converting {} to BigDecimal", string, e);
            return BigDecimal.ZERO;
        }
    }

    public Coin toCoin() {
        return Coin.builder()
                .value(new DecimalFormat("#.00").format(this.value))
                .diameter(new DecimalFormat("#.000").format(this.diameter))
                .weight(new DecimalFormat("#.000").format(this.weight))
                .build();
    }

    public static List<Currency> getValidDescendingSortedCurrencies() {
        return Arrays.stream(Currency.values())
                .filter(Currency::isValid)
                .sorted(comparing(Currency::getValue).reversed())
                .collect(Collectors.toList());
    }
}
