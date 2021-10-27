package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.event.CoinInsertedEvent;
import com.stevencarley.vendingdemo.model.Coin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CoinController {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CoinController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @MessageMapping("/coin")
    public void send(Coin coin) throws Exception {
        eventPublisher.publishEvent(new CoinInsertedEvent(this, coin));
    }
}
