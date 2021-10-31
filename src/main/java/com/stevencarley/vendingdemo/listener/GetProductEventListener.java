package com.stevencarley.vendingdemo.listener;

import com.stevencarley.vendingdemo.event.GetProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.stevencarley.vendingdemo.AppConstants.PRODUCT_TOPIC;

@Slf4j
@Component
public class GetProductEventListener {

    private final SimpMessagingTemplate template;

    @Autowired
    public GetProductEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void returnCoin(GetProductEvent getProductEvent) {
        log.debug("Received getProductEvent");
        this.template.convertAndSend(PRODUCT_TOPIC, getProductEvent.getProducts());
    }
}
