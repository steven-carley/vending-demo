package com.stevencarley.vendingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.stevencarley.vendingdemo.AppConstants.*;

@Service
public class MessageFormatterService {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$##0.00");

    private final ChangeService changeService;

    @Autowired
    public MessageFormatterService(ChangeService changeService) {
        this.changeService = changeService;
    }

    public String getDefaultMessage() {
        return changeService.canMakeChange() ? DEFAULT_MESSAGE : EXACT_CHANGE_MESSAGE;
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
