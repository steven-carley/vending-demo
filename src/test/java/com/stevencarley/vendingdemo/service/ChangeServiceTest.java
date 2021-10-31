package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Currency;
import com.stevencarley.vendingdemo.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stevencarley.vendingdemo.model.Currency.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeServiceTest {

    @InjectMocks
    ChangeService changeService;

    @Mock
    VendingEventPublisher eventPublisher;

    @Mock
    CurrencyRepository currencyRepository;

    @Mock
    TransactionService transactionService;

    Map<Currency, Long> currentCurrencies;

    @BeforeEach()
    void setUp() {
        currentCurrencies = new HashMap<>();
        currentCurrencies.put(QUARTER, 0L);
        currentCurrencies.put(DIME, 0L);
        currentCurrencies.put(NICKEL, 0L);
    }

    @Test
    void testMakeAndDispenseChangeWhenNotEnoughMoney() {
        assertThrows(IllegalArgumentException.class, () -> changeService.makeAndDispenseChange(BigDecimal.ONE, new BigDecimal(".5")));
    }

    @Test
    void testMakeAndDispenseChangeWhenHasChange() {
        when(transactionService.getCurrencies()).thenReturn(List.of(QUARTER, QUARTER, QUARTER, QUARTER));
        when(currencyRepository.makeChange(any())).thenReturn(List.of(QUARTER, QUARTER));
        changeService.makeAndDispenseChange(new BigDecimal(".75"), BigDecimal.ONE);
        verify(currencyRepository).addCurrencies(any());
        verify(currencyRepository).makeChange(new BigDecimal(".25"));
        verify(eventPublisher, times(2)).publishEvent(any(ReturnCoinEvent.class));
        verify(transactionService).clearCurrencies();
    }

    @Test
    void canMakeChangeReturnsFalseWhenNoCurrency() {
        when(currencyRepository.getCurrencyCount()).thenReturn(currentCurrencies);
        assertFalse(changeService.canMakeChange());
    }

    @Test
    void canMakeChangeReturnsFalseWhenOnlyQuarters() {
        when(currencyRepository.getCurrencyCount()).thenReturn(currentCurrencies);
        currentCurrencies.put(QUARTER, 10L);
        assertFalse(changeService.canMakeChange());
    }

    @Test
    void canMakeChangeReturnsFalseWhenOnlyDimes() {
        when(currencyRepository.getCurrencyCount()).thenReturn(currentCurrencies);
        currentCurrencies.put(DIME, 10L);;
        assertFalse(changeService.canMakeChange());
    }

    @Test
    void canMakeChangeReturnsTrueWhenOnlyNickels() {
        when(currencyRepository.getCurrencyCount()).thenReturn(currentCurrencies);
        currentCurrencies.put(NICKEL, 20L);
        assertTrue(changeService.canMakeChange());
    }

    @Test
    void canMakeChangeReturnsTrueHasEnoughCurrency() {
        when(currencyRepository.getCurrencyCount()).thenReturn(currentCurrencies);
        currentCurrencies.put(QUARTER, 3L);
        currentCurrencies.put(DIME, 3L);
        currentCurrencies.put(NICKEL, 3L);
        assertTrue(changeService.canMakeChange());
    }

    @Test
    void canMakeChangeReturnsFalseWhenNotEnoughCurrency() {
        when(currencyRepository.getCurrencyCount()).thenReturn(currentCurrencies);
        currentCurrencies.put(QUARTER, 2L);
        currentCurrencies.put(DIME, 1L);
        currentCurrencies.put(NICKEL, 2L);
        assertFalse(changeService.canMakeChange());
    }
}