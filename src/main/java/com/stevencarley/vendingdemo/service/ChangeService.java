package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Currency;
import com.stevencarley.vendingdemo.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ChangeService {
    private static final BigDecimal CHANGE_INCREMENTS = new BigDecimal("0.05");

    private final VendingEventPublisher eventPublisher;
    private final TransactionService transactionService;
    private final CurrencyRepository currencyRepository;

    public ChangeService(VendingEventPublisher eventPublisher,
                         TransactionService transactionService,
                         CurrencyRepository currencyRepository) {
        this.eventPublisher = eventPublisher;
        this.transactionService = transactionService;
        this.currencyRepository = currencyRepository;
    }

    public void makeAndDispenseChange(BigDecimal productCost, BigDecimal totalAmount) {
        currencyRepository.addCurrencies(transactionService.getCurrencies().toArray(new Currency[]{}));
        BigDecimal changeAmount = totalAmount.subtract(productCost);
        if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("productCost " + productCost + " must be less than totalAmount " + totalAmount);
        }
        dispenseChange(currencyRepository.makeChange(changeAmount));
        transactionService.clearCurrencies();
    }

    void dispenseChange(List<Currency> change) {
        change.stream().forEachOrdered(currency -> eventPublisher.publishEvent(new ReturnCoinEvent(this, currency.toCoin())));
    }

    public boolean canMakeChange() {
        var currencyCount = currencyRepository.getCurrencyCount();
        var quarters = currencyCount.get(Currency.QUARTER);
        var dimes = currencyCount.get(Currency.DIME);
        var nickels = currencyCount.get(Currency.NICKEL);
        BigDecimal changeAmount = CHANGE_INCREMENTS;
        while (changeAmount.compareTo(BigDecimal.ONE) <= 0) {
            if (!canMakeChange(changeAmount, quarters, dimes, nickels)) {
                return false;
            }
            changeAmount = changeAmount.add(CHANGE_INCREMENTS);
        }
        return true;
    }

    private boolean canMakeChange(BigDecimal changeAmount, long quarters, long dimes, long nickels) {
        while (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (changeAmount.compareTo(Currency.QUARTER.getValue()) >= 0 && quarters > 0) {
                changeAmount = changeAmount.subtract(Currency.QUARTER.getValue());
                quarters--;
                continue;
            }
            if (changeAmount.compareTo(Currency.DIME.getValue()) >= 0 && dimes > 0) {
                changeAmount = changeAmount.subtract(Currency.DIME.getValue());
                dimes--;
                continue;
            }
            if (changeAmount.compareTo(Currency.NICKEL.getValue()) >= 0 && nickels > 0) {
                changeAmount = changeAmount.subtract(Currency.NICKEL.getValue());
                nickels--;
                continue;
            }
            if (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
                return false;
            }
        }

        return true;
    }
}
