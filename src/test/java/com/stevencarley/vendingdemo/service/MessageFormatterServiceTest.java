package com.stevencarley.vendingdemo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.stevencarley.vendingdemo.AppConstants.DEFAULT_MESSAGE;
import static com.stevencarley.vendingdemo.AppConstants.EXACT_CHANGE_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageFormatterServiceTest {

    @InjectMocks
    MessageFormatterService messageFormatterService;

    @Mock
    ChangeService changeService;

    @Test
    void formatsMessageWhenNull() {
        when(changeService.canMakeChange()).thenReturn(true);
        String formattedMessage = messageFormatterService.formatAmountMessage(null);
        assertEquals(DEFAULT_MESSAGE, formattedMessage, "Expecting default message");
    }

    @Test
    void formatsMessageWhenZero() {
        when(changeService.canMakeChange()).thenReturn(true);
        String formattedMessage = messageFormatterService.formatAmountMessage(BigDecimal.ZERO);
        assertEquals(DEFAULT_MESSAGE, formattedMessage, "Expecting default message");
    }

    @Test
    void formatsMessageWhenNonNull() {
        String formattedMessage = messageFormatterService.formatAmountMessage(new BigDecimal(".50"));
        assertEquals("$0.50", formattedMessage, "Expecting formatted message");
    }

    @Test
    void defaultMessageWhenCanMakeChange() {
        when(changeService.canMakeChange()).thenReturn(true);
        assertEquals(DEFAULT_MESSAGE, messageFormatterService.getDefaultMessage(), "Expecting insert coin message");
    }

    @Test
    void defaultMessageWhenCanNotMakeChange() {
        when(changeService.canMakeChange()).thenReturn(false);
        assertEquals(EXACT_CHANGE_MESSAGE, messageFormatterService.getDefaultMessage(), "Expecting exact change message");
    }
}