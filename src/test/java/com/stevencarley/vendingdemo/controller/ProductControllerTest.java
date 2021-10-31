package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;

    @Test
    void purchaseProductWillPurchaseProduct() {
        productController.purchaseProduct("1");
        verify(productService).purchaseProduct("1");
    }
}