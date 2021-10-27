package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.service.MessageFormatterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;

import static com.stevencarley.vendingdemo.AppConstants.DISPLAY_TOPIC;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayEventListenerTest {

    @InjectMocks
    DisplayEventListener displayEventListener;

    @Mock
    MessageFormatterService messageFormatterService;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    UpdateDisplayEvent updateDisplayEvent;

    @Test
    void whenUpdateDisplayEventHasMessage() {
        when(updateDisplayEvent.getMessage()).thenReturn("message");
        when(updateDisplayEvent.getAmount()).thenReturn(null);
        displayEventListener.publishDisplayMessage(updateDisplayEvent);
        verify(simpMessagingTemplate).convertAndSend(DISPLAY_TOPIC, "message");
    }

    @Test
    void whenUpdateDisplayEventHasAmount() {
        String formattedValue = "$0.50";
        when(updateDisplayEvent.getMessage()).thenReturn(null);
        when(updateDisplayEvent.getAmount()).thenReturn(new BigDecimal("0.5"));
        when(messageFormatterService.formatAmountMessage(any())).thenReturn(formattedValue);
        displayEventListener.publishDisplayMessage(updateDisplayEvent);
        verify(simpMessagingTemplate).convertAndSend(DISPLAY_TOPIC, formattedValue);
    }

    @Test
    void whenUpdateDisplayEventNoAmountOrMessage() {
        String defaultMessage = "default message";
        when(updateDisplayEvent.getMessage()).thenReturn(null);
        when(updateDisplayEvent.getAmount()).thenReturn(null);
        when(messageFormatterService.getDefaultMessage()).thenReturn(defaultMessage);
        displayEventListener.publishDisplayMessage(updateDisplayEvent);
        verify(simpMessagingTemplate).convertAndSend(DISPLAY_TOPIC, defaultMessage);
    }
}