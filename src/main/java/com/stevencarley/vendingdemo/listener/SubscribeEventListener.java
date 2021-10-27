package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.service.TransactionService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static com.stevencarley.vendingdemo.AppConstants.DISPLAY_TOPIC;

@Component
public class SubscribeEventListener {

    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;

    public SubscribeEventListener(TransactionService transactionService, ApplicationEventPublisher eventPublisher) {
        this.transactionService = transactionService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onSessionSubscribedEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        Message<byte[]> message = sessionSubscribeEvent.getMessage();
        StompHeaderAccessor accessor = getStompHeaderAccessor(message);
        StompCommand command = accessor.getCommand();
        if (command.equals(StompCommand.SUBSCRIBE)) {
            String destination = accessor.getDestination();
            if (DISPLAY_TOPIC.equals(destination)) {
                eventPublisher.publishEvent(new UpdateDisplayEvent(this, transactionService.getTotalCurrencies()));
            }
        }
    }

    StompHeaderAccessor getStompHeaderAccessor(Message<byte[]> message) {
        return StompHeaderAccessor.wrap(message);
    }
}
