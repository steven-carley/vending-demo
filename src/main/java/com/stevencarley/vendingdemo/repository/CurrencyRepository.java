package com.stevencarley.vendingdemo.repository;

import com.stevencarley.vendingdemo.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Repository
public class CurrencyRepository {
    private final ConcurrentHashMap<Currency, Long> currencyCount = new ConcurrentHashMap<>();

    @Value("${currency.defaultCount}")
    private long defaultCurrencyCount;

    @PostConstruct
    public void initCurrencies() {
        Arrays.stream(Currency.values()).filter(Currency::isValid).forEach(currency -> currencyCount.put(currency, defaultCurrencyCount));
    }

    public Map<Currency, Long> getCurrencyCount() {
        return Collections.unmodifiableMap(currencyCount);
    }

    public synchronized void addCurrencies(Currency... currencies) {
        Arrays.stream(currencies)
                .collect(groupingBy(identity(), counting()))
                .entrySet().stream().forEach(entry -> {
                    var count = currencyCount.get(entry.getKey());
                    if (count == null) {
                        count = 0L;
                    }
                    currencyCount.put(entry.getKey(), count + entry.getValue());
                });
    }

    public synchronized void addCurrency(Currency currency, Long count) {
        var currentCount = currencyCount.get(currency);
        if (currentCount == null) {
            currentCount = 0L;
        }
        currencyCount.put(currency, currentCount + count);
    }

    public synchronized List<Currency> makeChange(BigDecimal changeAmount) {
        List<Currency> change = new ArrayList<>();
        var currencies = Currency.getValidDescendingSortedCurrencies();
        outer:
        for (Currency currency : currencies) {
            while (changeAmount.compareTo(BigDecimal.ZERO) > 0 && changeAmount.compareTo(currency.getValue()) >= 0) {
                var count = currencyCount.get(currency);
                if (count <= 0) {
                    continue outer;
                }
                changeAmount = changeAmount.subtract(currency.getValue());
                change.add(currency);
                currencyCount.put(currency, currencyCount.get(currency) - 1L);
            }
        }
        if (changeAmount.compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Returned incorrect change amount {}", changeAmount);
        }
        return change;
    }
}
