package com.stevencarley.vendingdemo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
public class MessageFormatterService {

    static final String DEFAULT_MESSAGE = "INSERT COIN";
    static final String PRICE_MESSAGE_PREFIX = "PRICE ";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$##0.00");

    public String getDefaultMessage() {
        return DEFAULT_MESSAGE;
    }

    public String formatAmountMessage(BigDecimal amount) {
        if (amount == null || BigDecimal.ZERO.compareTo(amount) == 0) {
            return getDefaultMessage();
        }
        return DECIMAL_FORMAT.format(amount);
    }

    public String formatPriceMessage(BigDecimal amount) {
        if (amount == null) {
            return PRICE_MESSAGE_PREFIX + DECIMAL_FORMAT.format(BigDecimal.ZERO);
        }
        return PRICE_MESSAGE_PREFIX + DECIMAL_FORMAT.format(amount);
    }
}
