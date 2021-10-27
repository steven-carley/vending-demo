package com.stevencarley.vendingdemo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
public class MessageFormatterService {

    static final String DEFAULT_MESSAGE = "INSERT COIN";
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
}
