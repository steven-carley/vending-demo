package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.model.Product;
import com.stevencarley.vendingdemo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void getProductsWillGetProducts() {
        var product = Product.builder().id("1").price(BigDecimal.ONE).description("test").build();
        when(productService.getProducts()).thenReturn(List.of(product));
        List<Product> result = productController.getProducts();
        assertEquals(List.of(product), result, "Expecting products to match");
        verify(productService).getProducts();
    }
}