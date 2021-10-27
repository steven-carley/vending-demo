package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.service.MessageFormatterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DisplayEventListener {

    private final MessageFormatterService messageFormatterService;
    private final SimpMessagingTemplate template;

    @Autowired
    public DisplayEventListener(MessageFormatterService messageFormatterService,
                                SimpMessagingTemplate template) {
        this.messageFormatterService = messageFormatterService;
        this.template = template;
    }

    @EventListener
    public void publishDisplayMessage(UpdateDisplayEvent updateDisplayEvent) {
        log.debug("Received updateDisplayEvent");
        String message = messageFormatterService.getDefaultMessage();
        if (updateDisplayEvent.getAmount() != null) {
            message = messageFormatterService.formatAmountMessage(updateDisplayEvent.getAmount());
        }
        if (updateDisplayEvent.getMessage() != null) {
            message = updateDisplayEvent.getMessage();
        }
        this.template.convertAndSend("/topic/display", message);
    }
}
