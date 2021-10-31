package com.stevencarley.vendingdemo.event;

import com.stevencarley.vendingdemo.model.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class GetProductEvent extends ApplicationEvent {

    private final List<Product> products;

    public GetProductEvent(Object source, List<Product> products) {
        super(source);
        this.products = products;
    }

}
