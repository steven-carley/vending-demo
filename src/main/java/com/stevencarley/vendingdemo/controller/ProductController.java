package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @MessageMapping("/purchase/{id}")
    public void purchaseProduct(@DestinationVariable String id) {
        productService.purchaseProduct(id);
    }

    @MessageMapping("/resetInventory")
    public void resetInventory() {
        productService.resetInventory();
    }
}
