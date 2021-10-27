package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.model.Currency;
import com.stevencarley.vendingdemo.event.CoinInsertedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoinsService {

    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CoinsService(TransactionService transactionService, ApplicationEventPublisher eventPublisher) {
        this.transactionService = transactionService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onCoinInsertedEvent(CoinInsertedEvent event) {
        log.info("Received coinInsertedEvent");
        Currency currency = Currency.getCurrency(event.getCoin());
        if (currency != null) {
            if (currency.isValid()) {
                transactionService.addToTransaction(currency);
            }
            else {
                eventPublisher.publishEvent(new ReturnCoinEvent(this, currency.toCoin()));
            }
        } else {
            eventPublisher.publishEvent(new ReturnCoinEvent(this, event.getCoin()));
        }
    }
}