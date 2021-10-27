package com.stevencarley.vendingdemo.event;

import org.springframework.context.ApplicationEvent;

public class ReturnAllCoinsEvent extends ApplicationEvent {
    public ReturnAllCoinsEvent(Object source) {
        super(source);
    }
}
