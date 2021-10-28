package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.event.CoinInsertedEvent;
import com.stevencarley.vendingdemo.event.ReturnAllCoinsEvent;
import com.stevencarley.vendingdemo.model.Coin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class CoinController {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CoinController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @MessageMapping("/coin")
    public void insertCoin(Coin coin) {
        log.debug("Received coin {}", coin);
        eventPublisher.publishEvent(new CoinInsertedEvent(this, coin));
    }

    @MessageMapping("/returncoins")
    public void returnCoins() {
        eventPublisher.publishEvent(new ReturnAllCoinsEvent(this));
    }
}
