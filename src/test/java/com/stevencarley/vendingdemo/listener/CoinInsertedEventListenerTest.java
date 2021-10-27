package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.CoinInsertedEvent;
import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.model.Coin;
import com.stevencarley.vendingdemo.model.Currency;
import com.stevencarley.vendingdemo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinInsertedEventListenerTest {

    @InjectMocks
    CoinInsertedEventListener coinInsertedEventListener;

    @Mock
    TransactionService transactionService;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    CoinInsertedEvent coinInsertedEvent;

    @Test
    void whenInsertedCoinIsValid() {
        when(coinInsertedEvent.getCoin()).thenReturn(Currency.DIME.toCoin());
        coinInsertedEventListener.onCoinInsertedEvent(coinInsertedEvent);
        verify(transactionService).addToTransaction(Currency.DIME);
    }

    @Test
    void whenInsertedCoinIsUnknown() {
        when(coinInsertedEvent.getCoin()).thenReturn(Coin.builder().weight(".001").diameter(".5").build());
        coinInsertedEventListener.onCoinInsertedEvent(coinInsertedEvent);
        verify(eventPublisher).publishEvent(any(ReturnCoinEvent.class));
    }

    @Test
    void whenInsertedCoinIsNotValidType() {
        when(coinInsertedEvent.getCoin()).thenReturn(Currency.PENNY.toCoin());
        coinInsertedEventListener.onCoinInsertedEvent(coinInsertedEvent);
        verify(eventPublisher).publishEvent(any(ReturnCoinEvent.class));
    }
}