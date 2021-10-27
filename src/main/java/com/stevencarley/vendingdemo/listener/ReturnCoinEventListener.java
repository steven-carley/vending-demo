package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.ReturnCoinEvent;
import com.stevencarley.vendingdemo.event.UpdateDisplayEvent;
import com.stevencarley.vendingdemo.service.MessageFormatterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReturnCoinEventListener {

    private final SimpMessagingTemplate template;

    @Autowired
    public ReturnCoinEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void returnCoin(ReturnCoinEvent returnCoinEvent) {
        log.debug("Received returnCoinEvent");
        this.template.convertAndSend("/topic/return", returnCoinEvent.getCoin());
    }
}
