package com.stevencarley.vendingdemo.repository;

import com.stevencarley.vendingdemo.config.ProductConfig;
import com.stevencarley.vendingdemo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;

    @Mock
    ProductConfig productConfig;

    List<Product> products;

    int defaultStock = 3;

    @BeforeEach
    void setUp() {
        products = new ArrayList<>();
        products.add(Product.builder().id("1").description("cola").price(new BigDecimal("1.00")).build());
        products.add(Product.builder().id("2").description("chips").price(new BigDecimal("0.50")).build());
        products.add(Product.builder().id("3").description("candy").price(new BigDecimal("0.65")).build());
    }

    @Test
    void getProductsReturnsProducts() {
        when(productConfig.getProducts()).thenReturn(products);
        assertEquals(products, productRepository.getProducts(), "Expecting products to match");
    }

    @Test
    void getProductReturnsProduct() {
        when(productConfig.getProducts()).thenReturn(products);
        assertEquals(products.get(0), productRepository.getProduct("1"), "Expecting product to match");
    }

    @Test
    void getProductCountReturnsCount() {
        when(productConfig.getProducts()).thenReturn(products);
        when(productConfig.getDefaultStock()).thenReturn(defaultStock);
        productRepository.initInventory();
        assertEquals(defaultStock, productRepository.getProductCount("1"), "Expecting count to match");
    }

    @Test
    void getProductCountReturnNull() {
        when(productConfig.getProducts()).thenReturn(products);
        when(productConfig.getDefaultStock()).thenReturn(defaultStock);
        productRepository.initInventory();
        assertNull(productRepository.getProductCount("5"), "Expecting count to be null");
    }


    @Test
    void buyProductReturnsFalseWhenCountIsZero() {
        when(productConfig.getProducts()).thenReturn(products);
        when(productConfig.getDefaultStock()).thenReturn(0);
        productRepository.initInventory();
        assertFalse(productRepository.buyProduct("1"), "Expecting false");
    }

    @Test
    void buyProductReturnsFalseWhenCountIsPositive() {
        when(productConfig.getProducts()).thenReturn(products);
        when(productConfig.getDefaultStock()).thenReturn(1);
        productRepository.initInventory();
        assertTrue(productRepository.buyProduct("1"), "Expecting true");
        assertEquals(0, productRepository.getProductCount("1"), "Expecting count to be 0");
    }
}