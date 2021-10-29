package com.stevencarley.vendingdemo.event.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VendingEventPublisherTest {

    @InjectMocks
    VendingEventPublisher vendingEventPublisher;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Mock
    TaskScheduler taskScheduler;

    @Test
    void willPublishEvent() {
        Object object = new Object();
        vendingEventPublisher.publishEvent(object);
        verify(applicationEventPublisher).publishEvent(object);
    }

    @Test
    void willPublishEventAfterDelay() {
        Object object = new Object();
        vendingEventPublisher.publishEventAfterDelay(object);
        verify(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
    }
}