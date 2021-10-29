package com.stevencarley.vendingdemo.event;

import com.stevencarley.vendingdemo.model.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DispenseProductEvent extends ApplicationEvent {

    private Product product;

    public DispenseProductEvent(Object source, Product product) {
        super(source);
        this.product = product;
    }
}
