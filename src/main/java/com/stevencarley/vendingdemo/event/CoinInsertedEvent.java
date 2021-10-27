package com.stevencarley.vendingdemo.event;

import com.stevencarley.vendingdemo.model.Coin;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CoinInsertedEvent extends ApplicationEvent {

    private Coin coin;

    public CoinInsertedEvent(Object source, Coin coin) {
        super(source);
        this.coin = coin;
    }
}
