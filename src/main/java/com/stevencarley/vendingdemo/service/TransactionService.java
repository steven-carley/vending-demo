package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.model.Currency;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class TransactionService {
    private Queue<Currency> currencies = new ConcurrentLinkedQueue<>();
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public TransactionService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public BigDecimal addToTransaction(Currency currency) {
        currencies.add(currency);
        BigDecimal total = getTotalCurrencies();
        UpdateDisplayEvent updateDisplayEvent = new UpdateDisplayEvent(this, total);
        eventPublisher.publishEvent(updateDisplayEvent);
        return total;
    }

    public BigDecimal getTotalCurrencies() {
        return currencies.stream().map(Currency::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void returnAllCoins() {
        var iterator = currencies.iterator();
        while(iterator.hasNext()) {
            eventPublisher.publishEvent(new ReturnCoinEvent(this, iterator.next().toCoin()));
            iterator.remove();
        }
    }
}
