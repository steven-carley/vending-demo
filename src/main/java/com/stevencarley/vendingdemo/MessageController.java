package com.stevencarley.vendingdemo;

import com.stevencarley.vendingdemo.model.Coin;
import com.stevencarley.vendingdemo.model.Message;
import com.stevencarley.vendingdemo.model.OutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Slf4j
public class MessageController {

    @MessageMapping("/coin")
    @SendTo("/topic/display")
    public String send(Coin coin) throws Exception {
        log.info("Saw coin {}", coin);
        return coin.getValue();
    }
}
