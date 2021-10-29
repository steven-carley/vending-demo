package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.DispenseProductEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.stevencarley.vendingdemo.AppConstants.THANK_YOU_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @InjectMocks
    ProductsService productsService;

    @Mock
    TransactionService transactionService;

    @Mock
    MessageFormatterService messageFormatterService;

    @Mock
    VendingEventPublisher eventPublisher;

    @Captor
    ArgumentCaptor<ApplicationEvent> applicationEventArgumentCaptor;

    List<Product> expectedProducts;

    @BeforeEach
    void setUp() {
        expectedProducts = new ArrayList<>();
        expectedProducts.add(Product.builder().id("1").description("cola").price(new BigDecimal("1.00")).build());
        expectedProducts.add(Product.builder().id("2").description("chips").price(new BigDecimal("0.50")).build());
        expectedProducts.add(Product.builder().id("3").description("candy").price(new BigDecimal("0.65")).build());
    }

    @Test
    void getProductsReturnsAllProducts() {
        List<Product> products = productsService.getProducts();
        assertEquals(expectedProducts, products, "Expecting products to match");
    }

    @Test
    void purchaseProductWhenIdIsNull() {
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        productsService.purchaseProduct(null);
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getAmount()),
                "Expecting update display based on amount");
    }

    @Test
    void purchaseProductWhenIdDoesNotMatch() {
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        productsService.purchaseProduct("not an id");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getAmount()),
                "Expecting update display based on amount");
    }

    @Test
    void purchaseProductWhenIdExistsAndHasFunds() {
        when(transactionService.getTotalCurrencies()).thenReturn(new BigDecimal("1.00"));
        productsService.purchaseProduct("2");
        verify(eventPublisher, times(2)).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(expectedProducts.get(1),
                ((DispenseProductEvent) applicationEventArgumentCaptor.getAllValues().get(0)).getProduct(),
                "Expecting product to match");
        assertEquals(THANK_YOU_MESSAGE,
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting thank you message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
    }

    @Test
    void purchaseProductWhenIdExistsAndDoesNotHaveFunds() {
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        when(messageFormatterService.formatPriceMessage(any())).thenReturn("PRICE $1.00");
        productsService.purchaseProduct("1");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals("PRICE $1.00",
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting price message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
    }
}