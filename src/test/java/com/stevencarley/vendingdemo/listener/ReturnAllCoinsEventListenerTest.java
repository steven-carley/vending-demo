package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.ReturnAllCoinsEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReturnAllCoinsEventListenerTest {

    @InjectMocks
    ReturnAllCoinsEventListener returnAllCoinsEventListener;

    @Mock
    TransactionService transactionService;

    @Mock
    VendingEventPublisher eventPublisher;

    @Mock
    ReturnAllCoinsEvent returnAllCoinsEvent;

    @Test
    void onReturnAllCoinsEventWillReturnCoinsAndUpdateDisplay() {
        returnAllCoinsEventListener.onReturnAllCoinsEvent(returnAllCoinsEvent);
        verify(transactionService).returnAllCoins();
        verify(eventPublisher).publishEvent(any(UpdateDisplayEvent.class));
    }
}