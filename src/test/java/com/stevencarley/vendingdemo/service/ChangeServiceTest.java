package com.stevencarley.vendingdemo.service;

import com.stevencarley.vendingdemo.event.publisher.VendingEventPublisher;
import com.stevencarley.vendingdemo.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.stevencarley.vendingdemo.model.Currency.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChangeServiceTest {

    @InjectMocks
    ChangeService changeService;

    @Mock
    VendingEventPublisher eventPublisher;

    @Test
    void testMakeChangeWhenAmountsEqual() {
        List<Currency> result = changeService.makeChange(BigDecimal.ZERO);
        assertEquals(0, result.size(), "Expecting no change");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfSingleQuarter() {
        List<Currency> result = changeService.makeChange(new BigDecimal(".25"));
        assertEquals(List.of(QUARTER), result, "Expecting 1 quarters");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfSingleDime() {
        List<Currency> result = changeService.makeChange(new BigDecimal(".1"));
        assertEquals(List.of(DIME), result, "Expecting 1 dime");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfSingleNickel() {
        List<Currency> result = changeService.makeChange(new BigDecimal(".05"));
        assertEquals(List.of(NICKEL), result, "Expecting 1 nickel");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfMultipleSame() {
        List<Currency> result = changeService.makeChange(new BigDecimal(".50"));
        assertEquals(List.of(QUARTER, QUARTER), result, "Expecting 2 quarters");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfMultiple() {
        List<Currency> result = changeService.makeChange(new BigDecimal(".85"));
        assertEquals(List.of(QUARTER, QUARTER, QUARTER, DIME), result, "Expecting 3 quarters 1 dime");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfAll3Types() {
        List<Currency> result = changeService.makeChange(new BigDecimal(".65"));
        assertEquals(List.of(QUARTER, QUARTER, DIME, NICKEL), result, "Expecting 2 quarters, 1 dime, 1 nickel");
    }
}