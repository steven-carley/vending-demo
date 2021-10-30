package com.stevencarley.vendingdemo.event.publisher;

import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.ScheduledFuture;

@Component
public class VendingEventPublisher {

    private final ApplicationEventPublisher delegate;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> lastFuture;

    @Value("${default.message.delay}")
    private long delay;

    @Autowired
    public VendingEventPublisher(ApplicationEventPublisher applicationEventPublisher,
                                 TaskScheduler taskScheduler) {
        this.delegate = applicationEventPublisher;
        this.taskScheduler = taskScheduler;
    }

    public <T extends ApplicationEvent> void publishEvent(T event) {
        if (event instanceof UpdateDisplayEvent && lastFuture != null && !lastFuture.isDone()) {
            lastFuture.cancel(false);
        }
        delegate.publishEvent(event);
    }

    public void publishEventAfterDelay(UpdateDisplayEvent event) {
        lastFuture = taskScheduler.schedule(() -> delegate.publishEvent(event),
                OffsetDateTime.now().plusSeconds(delay).toInstant());
    }
}
