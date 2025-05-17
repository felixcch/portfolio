package com.portfolio.service;

import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.model.instrument.option.OptionType;
import com.portfolio.pricer.OptionPricer;
import com.portfolio.pricer.OptionPricerFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PricingServiceTest {

    private PricingService pricingService;

    @BeforeAll
    public void setUp() {
        pricingService = new PricingService();
    }

    @Test
    public void generateRandomPrice_shouldGeneratePriceWithinReasonableRange() {
        // Setup
        double currentPrice = 100.0;
        double annualizedSd = 0.5;
        double expectedReturn = 0.2;
        double dT = 60; // 1 minute

        // Act
        double newPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);

        // Assert
        assertTrue(newPrice > 0);
        assertTrue(newPrice > currentPrice * 0.5 && newPrice < currentPrice * 1.5);
    }

    @Test
    public void priceOption_shouldInvokeOptionPricer() {
        // Setup
        EuropeanOption option = new EuropeanOption();
        option.setOptionType(OptionType.CALL);
        option.setStrikePrice(100.0);
        option.setStrikeDate(LocalDate.now().plusDays(365));
        double underlyingPrice = 150.0;
        double underlyingSd = 0.5;

        // Mock OptionPricerFactory
        OptionPricer pricer = mock(OptionPricer.class);
        when(pricer.price(eq(OptionType.CALL), eq(150.0), eq(100.0), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(50.0);
        try (MockedStatic<OptionPricerFactory> mockedFactory = mockStatic(OptionPricerFactory.class)) {
            mockedFactory.when(() -> OptionPricerFactory.getPricer(option)).thenReturn(pricer);

            // Act
            double price = pricingService.priceOption(option, underlyingPrice, underlyingSd);

            // Assert
            assertEquals(50.0, price, 0.01);
        }
    }

    @Test
    public void generateRandomPrice_shouldGeneratePositivePriceWithinReasonableRange() {
        // Setup: Typical stock parameters
        double currentPrice = 100.0;
        double annualizedSd = 0.5; // 50% volatility
        double expectedReturn = 0.2; // 20% annual return
        double dT = 60.0; // 1 minute

        // Act: Run multiple iterations to account for randomness
        int iterations = 1000;
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        for (int i = 0; i < iterations; i++) {
            double newPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);
            assertTrue(newPrice > 0);
            minPrice = Math.min(minPrice, newPrice);
            maxPrice = Math.max(maxPrice, newPrice);
        }

        // Assert: Prices should be within ±3 standard deviations (covering ~99.7% of Gaussian distribution)
        double timeFraction = dT / 7257600; // Convert minutes to years
        double expectedDrift = currentPrice * (expectedReturn * timeFraction);
        double stdDev = currentPrice * annualizedSd * Math.sqrt(timeFraction);
        double lowerBound = currentPrice + expectedDrift - 3 * stdDev;
        double upperBound = currentPrice + expectedDrift + 3 * stdDev;
        assertTrue(minPrice >= lowerBound * 0.9); // Allow 10% margin
        assertTrue(maxPrice <= upperBound * 1.1); // Allow 10% margin
    }

    @Test
    public void generateRandomPrice_withZeroDt_shouldReturnCurrentPricePlusSmallDrift() {
        // Setup: dT = 0, so volatility term should be zero
        double currentPrice = 100.0;
        double annualizedSd = 0.5;
        double expectedReturn = 0.2;
        double dT = 0.0;

        // Act
        double newPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);

        // Assert: With dT=0, price change is only due to drift (which is zero)
        assertEquals(currentPrice, newPrice, 0.01);
    }

    @Test
    public void generateRandomPrice_withZeroCurrentPrice_shouldReturnZero() {
        // Setup: currentPrice = 0
        double currentPrice = 0.0;
        double annualizedSd = 0.5;
        double expectedReturn = 0.2;
        double dT = 60.0;

        // Act
        double newPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);

        // Assert: If currentPrice=0, price change should be zero
        assertEquals(0.0, newPrice, 0.01);
    }

    @Test
    public void generateRandomPrice_withNegativeSd_shouldProduceValidPrice() {
        // Setup: Negative annualizedSd (invalid but should not crash)
        double currentPrice = 100.0;
        double annualizedSd = -0.5;
        double expectedReturn = 0.2;
        double dT = 60.0;

        // Act
        double newPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);

        // Assert: Price should still be positive and within a reasonable range
        assertTrue(newPrice > 0);
        // Note: Negative SD may flip the volatility effect, but price should remain valid
        double timeFraction = dT / 7257600;
        double expectedDrift = currentPrice * (expectedReturn * timeFraction);
        double stdDev = currentPrice * Math.abs(annualizedSd) * Math.sqrt(timeFraction);
        double lowerBound = currentPrice + expectedDrift - 3 * stdDev;
        assertTrue(newPrice >= lowerBound * 0.9);
    }

    @Test
    public void generateRandomPrice_shouldNeverReturnNegativePrice() {
        // Setup: Various input scenarios to stress the method
        double[][] testCases = {
                {100.0, 0.5, 0.2, 60.0},        // Normal case: moderate volatility, 1 minute
                {100.0, 2.0, -0.5, 3600.0},     // High volatility, negative return, 1 hour
                {10.0, 1.0, 0.0, 86400.0},      // Low price, large time step (1 day)
                {1000.0, 0.1, 0.1, 60.0},       // High price, low volatility
                {0.0, 0.5, 0.2, 60.0},          // Zero current price
                {100.0, -0.5, 0.2, 60.0},       // Negative volatility
                {100.0, 0.5, -1.0, 3600.0}      // Strong negative return
        };

        // Act & Assert: Run 10,000 iterations per test case
        int iterations = 10000;
        for (double[] testCase : testCases) {
            double currentPrice = testCase[0];
            double annualizedSd = testCase[1];
            double expectedReturn = testCase[2];
            double dT = testCase[3];

            for (int i = 0; i < iterations; i++) {
                double newPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);
                assertTrue(newPrice > 0);
            }
        }
    }
}
