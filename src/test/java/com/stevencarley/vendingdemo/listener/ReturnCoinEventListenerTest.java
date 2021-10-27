package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.model.Coin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static com.stevencarley.vendingdemo.AppConstants.RETURN_TOPIC;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnCoinEventListenerTest {

    @InjectMocks
    ReturnCoinEventListener returnCoinEventListener;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    ReturnCoinEvent returnCoinEvent;

    @Test
    void willSendMessageToTopic() {
        Coin coin = Coin.builder().build();
        when(returnCoinEvent.getCoin()).thenReturn(coin);
        returnCoinEventListener.returnCoin(returnCoinEvent);
        verify(simpMessagingTemplate).convertAndSend(RETURN_TOPIC, coin);
    }
}