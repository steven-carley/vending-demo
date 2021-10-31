package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.DispenseProductEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Product;
import com.stevencarley.vendingdemo.repository.ProductRepository;
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

import static com.stevencarley.vendingdemo.AppConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    TransactionService transactionService;

    @Mock
    MessageFormatterService messageFormatterService;

    @Mock
    VendingEventPublisher eventPublisher;

    @Mock
    ChangeService changeService;

    @Mock
    ProductRepository productRepository;

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
        when(productRepository.getProducts()).thenReturn(expectedProducts);
        List<Product> products = productService.getProducts();
        assertEquals(expectedProducts, products, "Expecting products to match");
        verify(productRepository).getProducts();
    }

    @Test
    void purchaseProductWhenIdIsNull() {
        when(productRepository.getProduct(any())).thenReturn(null);
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        productService.purchaseProduct(null);
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getAmount()),
                "Expecting update display based on amount");
        verify(productRepository).getProduct(null);
    }

    @Test
    void purchaseProductWhenIdDoesNotMatch() {
        when(productRepository.getProduct(any())).thenReturn(null);
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        productService.purchaseProduct("not an id");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(0, BigDecimal.ZERO.compareTo(
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getAmount()),
                "Expecting update display based on amount");
        verify(productRepository).getProduct("not an id");
    }

    @Test
    void purchaseProductWhenIdExistsAndHasFundsAndHasInventory() {
        when(changeService.canMakeChange()).thenReturn(true);
        when(productRepository.getProduct(any())).thenReturn(expectedProducts.get(1));
        when(productRepository.getProductCount(any())).thenReturn(1);
        when(productRepository.buyProduct(any())).thenReturn(true);
        when(transactionService.getTotalCurrencies()).thenReturn(new BigDecimal("1.00"));
        productService.purchaseProduct("2");
        verify(eventPublisher, times(2)).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(expectedProducts.get(1),
                ((DispenseProductEvent) applicationEventArgumentCaptor.getAllValues().get(0)).getProduct(),
                "Expecting product to match");
        assertEquals(THANK_YOU_MESSAGE,
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting thank you message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
        verify(productRepository).getProduct("2");
        verify(productRepository).buyProduct("2");
        verify(changeService).makeAndDispenseChange(any(), any());
    }

    @Test
    void purchaseProductWhenIdExistsAndHasFundsAndHasInventoryAndCannotMakeChange() {
        when(changeService.canMakeChange()).thenReturn(false);
        when(productRepository.getProduct(any())).thenReturn(expectedProducts.get(1));
        when(productRepository.getProductCount(any())).thenReturn(1);
        when(transactionService.getTotalCurrencies()).thenReturn(new BigDecimal("1.00"));
        productService.purchaseProduct("2");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(EXACT_CHANGE_MESSAGE,
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting thank you message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
        verify(productRepository).getProduct("2");
        verify(productRepository, never()).buyProduct(any());
        verify(changeService, never()).makeAndDispenseChange(any(), any());
    }

    @Test
    void purchaseProductWhenIdExistsAndNoInventory() {
        when(productRepository.getProduct(any())).thenReturn(expectedProducts.get(1));
        when(productRepository.getProductCount(any())).thenReturn(0);
        productService.purchaseProduct("2");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(SOLD_OUT_MESSAGE,
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting sold out message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
    }

    @Test
    void purchaseProductWhenIdExistsAndHasFundsAndDoesNotHaveInventory() {
        when(changeService.canMakeChange()).thenReturn(true);
        when(productRepository.getProduct(any())).thenReturn(expectedProducts.get(1));
        when(productRepository.getProductCount(any())).thenReturn(1);
        when(productRepository.buyProduct(any())).thenReturn(false);
        when(transactionService.getTotalCurrencies()).thenReturn(new BigDecimal("1.00"));
        productService.purchaseProduct("2");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals(SOLD_OUT_MESSAGE,
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting sold out message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
        verify(productRepository).getProduct("2");
        verify(productRepository).buyProduct("2");
    }

    @Test
    void purchaseProductWhenIdExistsAndDoesNotHaveFunds() {
        when(changeService.canMakeChange()).thenReturn(true);
        when(productRepository.getProduct(any())).thenReturn(expectedProducts.get(0));
        when(productRepository.getProductCount(any())).thenReturn(1);
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        when(messageFormatterService.formatPriceMessage(any())).thenReturn("PRICE $1.00");
        productService.purchaseProduct("1");
        verify(eventPublisher).publishEvent(applicationEventArgumentCaptor.capture());
        assertEquals("PRICE $1.00",
                ((UpdateDisplayEvent) applicationEventArgumentCaptor.getValue()).getMessage(),
                "Expecting price message");
        verify(eventPublisher).publishEventAfterDelay(any(UpdateDisplayEvent.class));
    }
}