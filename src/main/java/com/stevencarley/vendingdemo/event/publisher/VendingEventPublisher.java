package com.stevencarley.vendingdemo.event.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class VendingEventPublisher {

    private final ApplicationEventPublisher delegate;
    private final TaskScheduler taskScheduler;

    @Value("${default.message.delay}")
    private long delay;

    @Autowired
    public VendingEventPublisher(ApplicationEventPublisher applicationEventPublisher,
                                 TaskScheduler taskScheduler) {
        this.delegate = applicationEventPublisher;
        this.taskScheduler = taskScheduler;
    }

    public void publishEvent(Object object) {
        delegate.publishEvent(object);
    }

    public void publishEventAfterDelay(Object object) {
        taskScheduler.schedule(() -> delegate.publishEvent(object),
                OffsetDateTime.now().plusSeconds(delay).toInstant());
    }
}
