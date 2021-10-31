package com.stevencarley.vendingdemo.repository;

import com.stevencarley.vendingdemo.model.Currency;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRepositoryTest {

    @InjectMocks
    CurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 3L);
        currencyRepository.initCurrencies();
    }

    @Test
    void addCurrenciesAddsCorrectCurrencies() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 0L);
        currencyRepository.initCurrencies();
        currencyRepository.addCurrencies(Currency.DIME, Currency.QUARTER, Currency.DIME, Currency.NICKEL, Currency.NICKEL, Currency.NICKEL);
        ConcurrentHashMap<Currency, Long> currencyCount = (ConcurrentHashMap<Currency, Long>) ReflectionTestUtils.getField(currencyRepository, "currencyCount");
        assertEquals(3, currencyCount.size(), "Expecting size to match");
        assertEquals(1L, currencyCount.get(Currency.QUARTER), "Expecting number of quarters to match");
        assertEquals(2L, currencyCount.get(Currency.DIME), "Expecting number of dimes to match");
        assertEquals(3L, currencyCount.get(Currency.NICKEL), "Expecting number of nickels to match");
    }

    @Test
    void addCurrencyAddsCorrectCurrency() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 1L);
        currencyRepository.initCurrencies();
        currencyRepository.addCurrency(Currency.QUARTER, 1L);
        ConcurrentHashMap<Currency, Long> currencyCount = (ConcurrentHashMap<Currency, Long>) ReflectionTestUtils.getField(currencyRepository, "currencyCount");
        assertEquals(3, currencyCount.size(), "Expecting size to match");
        assertEquals(2L, currencyCount.get(Currency.QUARTER), "Expecting number of quarters to match");
    }

    @Test
    void makeChangeReturnsExactChangeForQuarter() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 1L);
        currencyRepository.initCurrencies();
        var result = currencyRepository.makeChange(Currency.QUARTER.getValue());
        assertEquals(List.of(Currency.QUARTER), result, "Expecting 1 quarter back");
    }

    @Test
    void makeChangeReturnsExactChangeForDime() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 1L);
        currencyRepository.initCurrencies();
        var result = currencyRepository.makeChange(Currency.DIME.getValue());
        assertEquals(List.of(Currency.DIME), result, "Expecting 1 dime back");
    }

    @Test
    void makeChangeReturnsExactChangeForNickel() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 1L);
        currencyRepository.initCurrencies();
        var result = currencyRepository.makeChange(Currency.NICKEL.getValue());
        assertEquals(List.of(Currency.NICKEL), result, "Expecting 1 nickel back");
    }

    @Test
    void makeChangeReturnsChangeForMultipleCoins() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 3L);
        currencyRepository.initCurrencies();
        var results = currencyRepository.makeChange(new BigDecimal(".40"));
        assertEquals(List.of(Currency.QUARTER, Currency.DIME, Currency.NICKEL), results, "Expecting change to match");
    }

    @Test
    void makeChangeReturnsChangeForMultipleCoinsNoDimes() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 0L);
        currencyRepository.initCurrencies();
        currencyRepository.addCurrencies(Currency.QUARTER, Currency.NICKEL, Currency.NICKEL, Currency.NICKEL, Currency.NICKEL);
        var results = currencyRepository.makeChange(new BigDecimal(".40"));
        assertEquals(List.of(Currency.QUARTER, Currency.NICKEL, Currency.NICKEL, Currency.NICKEL), results, "Expecting change to match");
    }

    @Test
    void makeChangeReturnsChangeForMultipleCoinsNoQuarters() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 0L);
        currencyRepository.initCurrencies();
        currencyRepository.addCurrency(Currency.DIME, 10L);
        currencyRepository.addCurrency(Currency.NICKEL, 10L);
        var results = currencyRepository.makeChange(new BigDecimal(".55"));
        assertEquals(List.of(Currency.DIME, Currency.DIME, Currency.DIME, Currency.DIME, Currency.DIME, Currency.NICKEL), results, "Expecting change to match");
    }

    @Test
    void makeChangeReturnsChangeWhenNotEnough() {
        ReflectionTestUtils.setField(currencyRepository,"defaultCurrencyCount", 0L);
        currencyRepository.initCurrencies();
        var results = currencyRepository.makeChange(new BigDecimal(".55"));
        assertEquals(Lists.emptyList(), results, "Expecting no change");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfSingleQuarter() {
        List<Currency> result = currencyRepository.makeChange(new BigDecimal(".25"));
        assertEquals(List.of(Currency.QUARTER), result, "Expecting 1 quarters");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfSingleDime() {
        List<Currency> result = currencyRepository.makeChange(new BigDecimal(".1"));
        assertEquals(List.of(Currency.DIME), result, "Expecting 1 dime");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfSingleNickel() {
        List<Currency> result = currencyRepository.makeChange(new BigDecimal(".05"));
        assertEquals(List.of(Currency.NICKEL), result, "Expecting 1 nickel");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfMultipleSame() {
        List<Currency> result = currencyRepository.makeChange(new BigDecimal(".50"));
        assertEquals(List.of(Currency.QUARTER, Currency.QUARTER), result, "Expecting 2 quarters");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfMultiple() {
        List<Currency> result = currencyRepository.makeChange(new BigDecimal(".85"));
        assertEquals(List.of(Currency.QUARTER, Currency.QUARTER, Currency.QUARTER, Currency.DIME), result, "Expecting 3 quarters 1 dime");
    }

    @Test
    void testMakeChangeWhenExpectingChangeOfAll3Types() {
        List<Currency> result = currencyRepository.makeChange(new BigDecimal(".65"));
        assertEquals(List.of(Currency.QUARTER, Currency.QUARTER, Currency.DIME, Currency.NICKEL), result, "Expecting 2 quarters, 1 dime, 1 nickel");
    }
}