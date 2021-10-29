package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ProductController {

    private final ProductsService productsService;

    @Autowired
    public ProductController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @MessageMapping("/purchase/{id}")
    public void purchaseProduct(@DestinationVariable String id) {
        productsService.purchaseProduct(id);
    }
}
