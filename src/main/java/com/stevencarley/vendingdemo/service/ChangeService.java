package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
public class ChangeService {

    private final VendingEventPublisher eventPublisher;
    private final TransactionService transactionService;

    public ChangeService(VendingEventPublisher eventPublisher,
                         TransactionService transactionService) {
        this.eventPublisher = eventPublisher;
        this.transactionService = transactionService;
    }

    public void makeAndDispenseChange(BigDecimal productCost, BigDecimal totalAmount) {
        BigDecimal changeAmount = totalAmount.subtract(productCost);
        if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("productCost " + productCost + " must be less than totalAmount " + totalAmount);
        }
        dispenseChange(makeChange(changeAmount));
        transactionService.clearCurrencies();
    }

    List<Currency> makeChange(BigDecimal changeAmount) {

        List<Currency> change = new ArrayList<>();
        for (Currency currency : getSortedValidCurrencies()) {
            while (changeAmount.subtract(currency.getValue()).compareTo(BigDecimal.ZERO) >= 0) {
                change.add(currency);
                changeAmount = changeAmount.subtract(currency.getValue());
            }
        }
        return change;
    }

    void dispenseChange(List<Currency> change) {
        change.stream().forEachOrdered(currency -> eventPublisher.publishEvent(new ReturnCoinEvent(this, currency.toCoin())));
    }

    private List<Currency> getSortedValidCurrencies() {
        return Arrays.stream(Currency.values())
                .filter(Currency::isValid)
                .sorted(comparing(Currency::getValue).reversed())
                .collect(Collectors.toList());
    }
}
