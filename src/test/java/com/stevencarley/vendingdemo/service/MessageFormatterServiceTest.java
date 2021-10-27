package com.stevencarley.vendingdemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MessageFormatterServiceTest {

    MessageFormatterService messageFormatterService;

    @BeforeEach
    void setUp() {
        messageFormatterService = new MessageFormatterService();
    }

    @Test
    void formatsMessageWhenNull() {
        String formattedMessage = messageFormatterService.formatAmountMessage(null);
        assertEquals(MessageFormatterService.DEFAULT_MESSAGE, formattedMessage, "Expecting default message");
    }

    @Test
    void formatsMessageWhenZero() {
        String formattedMessage = messageFormatterService.formatAmountMessage(BigDecimal.ZERO);
        assertEquals(MessageFormatterService.DEFAULT_MESSAGE, formattedMessage, "Expecting default message");
    }

    @Test
    void formatsMessageWhenNonNull() {
        String formattedMessage = messageFormatterService.formatAmountMessage(new BigDecimal(".50"));
        assertEquals("$0.50", formattedMessage, "Expecting formatted message");
    }

    @Test
    void defaultMessage() {
        assertEquals(MessageFormatterService.DEFAULT_MESSAGE, messageFormatterService.getDefaultMessage(), "Expecting default message");
    }
}