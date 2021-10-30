package com.stevencarley.vendingdemo.event.publisher;

import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendingEventPublisherTest {

    @InjectMocks
    VendingEventPublisher vendingEventPublisher;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Mock
    TaskScheduler taskScheduler;

    @Mock
    ScheduledFuture<?> scheduledFuture;

    @Test
    void willPublishEvent() {
        TestApplicationEvent testApplicationEvent = new TestApplicationEvent(new Object());
        vendingEventPublisher.publishEvent(testApplicationEvent);
        verify(applicationEventPublisher).publishEvent(testApplicationEvent);
    }

    @Test
    void PublishEventWillCancelPendingFutureOnUpdateDisplayEvent() {
        when(scheduledFuture.isDone()).thenReturn(false);
        ReflectionTestUtils.setField(vendingEventPublisher, "lastFuture", scheduledFuture);
        UpdateDisplayEvent updateDisplayEvent = new UpdateDisplayEvent(this, "test");
        vendingEventPublisher.publishEvent(updateDisplayEvent);
        verify(applicationEventPublisher).publishEvent(updateDisplayEvent);
        verify(scheduledFuture).cancel(false);
    }

    @Test
    void PublishEventWillNotCancelPendingFutureOnUpdateDisplayEventAndIsDone() {
        when(scheduledFuture.isDone()).thenReturn(true);
        ReflectionTestUtils.setField(vendingEventPublisher, "lastFuture", scheduledFuture);
        UpdateDisplayEvent updateDisplayEvent = new UpdateDisplayEvent(this, "test");
        vendingEventPublisher.publishEvent(updateDisplayEvent);
        verify(applicationEventPublisher).publishEvent(updateDisplayEvent);
        verify(scheduledFuture, never()).cancel(false);
    }

    @Test
    void willPublishEventAfterDelay() {
        UpdateDisplayEvent updateDisplayEvent = new UpdateDisplayEvent(this, "test");
        vendingEventPublisher.publishEventAfterDelay(updateDisplayEvent);
        verify(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
    }

    private static final class TestApplicationEvent extends  ApplicationEvent {

        public TestApplicationEvent(Object source) {
            super(source);
        }
    }
}