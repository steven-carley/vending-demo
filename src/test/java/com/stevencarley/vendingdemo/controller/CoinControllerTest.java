package com.stevencarley.vendingdemo.controller;

import com.stevencarley.vendingdemo.event.CoinInsertedEvent;
import com.stevencarley.vendingdemo.event.ReturnAllCoinsEvent;
import com.stevencarley.vendingdemo.model.Coin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CoinControllerTest {

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    CoinController coinController;

    @Captor
    ArgumentCaptor<CoinInsertedEvent> coinArgumentCaptor;

    @Test
    void eventIsPublishedOnInsertCoin() {
        Coin coin = Coin.builder().value("test").build();
        coinController.insertCoin(coin);
        verify(eventPublisher).publishEvent(coinArgumentCaptor.capture());
        assertEquals(coin, coinArgumentCaptor.getValue().getCoin(), "Expecting coin to be passed to event");
    }

    @Test
    void eventIsPublishedOnReturnCoins() {
        coinController.returnCoins();
        verify(eventPublisher).publishEvent(any(ReturnAllCoinsEvent.class));
    }
}