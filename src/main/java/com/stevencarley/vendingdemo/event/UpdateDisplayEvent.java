package com.stevencarley.vendingdemo.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class UpdateDisplayEvent extends ApplicationEvent {

    private String message;
    private BigDecimal amount;

    public UpdateDisplayEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public UpdateDisplayEvent(Object source, BigDecimal amount) {
        super(source);
        this.amount = amount;
    }
}
