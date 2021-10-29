package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.model.Product;
import com.stevencarley.vendingdemo.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(originPatterns = "http://localhost:*")
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

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productsService.getProducts();
    }
}
