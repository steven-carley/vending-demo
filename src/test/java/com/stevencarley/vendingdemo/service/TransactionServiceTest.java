package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    TransactionService transactionService;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Test
    void addToTransactionWillAddCurrency() {
        BigDecimal total = transactionService.addToTransaction(Currency.DIME);
        verify(applicationEventPublisher).publishEvent(any(UpdateDisplayEvent.class));
        assertEquals(new BigDecimal("0.10").compareTo(total), 0, "Expecting total to match");
    }

    @Test
    void addToTransactionWillNotAddUnknownCurrency() {
        BigDecimal total = transactionService.addToTransaction(Currency.UNKNOWN);
        verify(applicationEventPublisher, never()).publishEvent(any(UpdateDisplayEvent.class));
        assertEquals(BigDecimal.ZERO.compareTo(total), 0, "Expecting zero total");
    }

    @Test
    void addToTransactionWillNotAddNull() {
        BigDecimal total = transactionService.addToTransaction(null);
        verify(applicationEventPublisher, never()).publishEvent(any(UpdateDisplayEvent.class));
        assertEquals(BigDecimal.ZERO.compareTo(total), 0, "Expecting zero total");
    }

    @Test
    void getTotalCurrenciesReturnsZeroWhenNoCurrency() {
        assertEquals(BigDecimal.ZERO.compareTo(transactionService.getTotalCurrencies()), 0, "Expecting zero total");
    }

    @Test
    void getTotalCurrenciesReturnsSumWhenHasCurrencies() {
        transactionService.addToTransaction(Currency.DIME);
        transactionService.addToTransaction(Currency.NICKEL);
        assertEquals(new BigDecimal(".15").compareTo(transactionService.getTotalCurrencies()), 0, "Expecting total to match");
    }

    @Test
    void returnAllCoinsWillRemoveAndReturnCoins() {
        transactionService.addToTransaction(Currency.DIME);
        transactionService.addToTransaction(Currency.NICKEL);
        transactionService.returnAllCoins();
        verify(applicationEventPublisher, times(2)).publishEvent(any(ReturnCoinEvent.class));
        assertEquals(0, transactionService.getNumberOfCurrencies(), "Expecting no currencies");
    }
}