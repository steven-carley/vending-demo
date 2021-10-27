package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.math.BigDecimal;

import static com.stevencarley.vendingdemo.AppConstants.DISPLAY_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscribeEventListenerTest {

    @InjectMocks
    SubscribeEventListener subscribeEventListener;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    TransactionService transactionService;

    @Mock
    SessionSubscribeEvent sessionSubscribeEvent;

    @Mock
    Message<byte[]> message;

    @Mock
    StompHeaderAccessor stompHeaderAccessor;

    @Test
    void willUpdateDisplayEventOnDisplayTopicSessionSubscribe() {
        SubscribeEventListener subscribeEventListenerSpy = spy(subscribeEventListener);
        when(sessionSubscribeEvent.getMessage()).thenReturn(message);
        when(subscribeEventListenerSpy.getStompHeaderAccessor(any())).thenReturn(stompHeaderAccessor);
        when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.SUBSCRIBE);
        when(stompHeaderAccessor.getDestination()).thenReturn(DISPLAY_TOPIC);
        when(transactionService.getTotalCurrencies()).thenReturn(BigDecimal.ZERO);
        subscribeEventListenerSpy.onSessionSubscribedEvent(sessionSubscribeEvent);
        verify(eventPublisher).publishEvent(any(UpdateDisplayEvent.class));
    }

    @Test
    void willNotUpdateDisplayEventOnOtherTopicSessionSubscribe() {
        SubscribeEventListener subscribeEventListenerSpy = spy(subscribeEventListener);
        when(sessionSubscribeEvent.getMessage()).thenReturn(message);
        when(subscribeEventListenerSpy.getStompHeaderAccessor(any())).thenReturn(stompHeaderAccessor);
        when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.SUBSCRIBE);
        when(stompHeaderAccessor.getDestination()).thenReturn("/topic/other");
        subscribeEventListenerSpy.onSessionSubscribedEvent(sessionSubscribeEvent);
        verify(eventPublisher, never()).publishEvent(any(UpdateDisplayEvent.class));
    }
}