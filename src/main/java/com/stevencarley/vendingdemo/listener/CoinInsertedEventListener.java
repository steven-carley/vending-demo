package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.model.Currency;
import com.stevencarley.vendingdemo.event.CoinInsertedEvent;
import com.stevencarley.vendingdemo.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoinInsertedEventListener {

    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CoinInsertedEventListener(TransactionService transactionService, ApplicationEventPublisher eventPublisher) {
        this.transactionService = transactionService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onCoinInsertedEvent(CoinInsertedEvent event) {
        log.debug("Received coinInsertedEvent");
        Currency currency = Currency.getCurrency(event.getCoin());
        if (currency == Currency.UNKNOWN) {
            eventPublisher.publishEvent(new ReturnCoinEvent(this, event.getCoin()));
        } else if (currency.isValid()) {
            transactionService.addToTransaction(currency);
        } else {
            eventPublisher.publishEvent(new ReturnCoinEvent(this, currency.toCoin()));
        }
    }
}