package com.portfolio.pricer;

import com.portfolio.model.instrument.option.OptionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class EuropeanOptionPricerTest {

    @InjectMocks
    private EuropeanOptionPricer europeanOptionPricer;
    // Unit Test: EuropeanOptionPricer - Call Option
    @Test
    void testEuropeanOptionPricerCall() {
        double underlyingPrice = 100.0;
        double strikePrice = 100.0;
        double yearToMaturity = 1.0;
        double riskFreeRate = 0.02;
        double sigma = 0.2;

        double price = europeanOptionPricer.price(
                OptionType.CALL, underlyingPrice, strikePrice, yearToMaturity, riskFreeRate, sigma);

        // Expected price based on Black-Scholes (approximate, verified with external calculator)
        assertTrue(price > 0, "Call option price should be positive");
        assertEquals(8.02, price, 0.1, "Call option price mismatch");
    }

    // Unit Test: EuropeanOptionPricer - Put Option
    @Test
    void testEuropeanOptionPricerPut() {
        double underlyingPrice = 100.0;
        double strikePrice = 100.0;
        double yearToMaturity = 1.0;
        double riskFreeRate = 0.02;
        double sigma = 0.2;

        double price = europeanOptionPricer.price(
                OptionType.PUT, underlyingPrice, strikePrice, yearToMaturity, riskFreeRate, sigma);

        // Expected price based on Black-Scholes (approximate, verified with external calculator)
        assertTrue(price > 0, "Put option price should be positive");
        assertEquals(5.57, price, 0.1, "Put option price mismatch");
    }
}
