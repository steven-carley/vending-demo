package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.ReturnAllCoinsEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReturnAllCoinsEventListener {
    private final TransactionService transactionService;
    private final VendingEventPublisher eventPublisher;

    @Autowired
    public ReturnAllCoinsEventListener(TransactionService transactionService, VendingEventPublisher eventPublisher) {
        this.transactionService = transactionService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onReturnAllCoinsEvent(ReturnAllCoinsEvent event) {
        transactionService.returnAllCoins();
        eventPublisher.publishEvent(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
    }
}
