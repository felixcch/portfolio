package com.portfolio.pricer;

import com.portfolio.model.instrument.option.EuropeanOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class OptionPricerFactoryTest {
    @Test
    void testEuropeanOptionPricer() {
        EuropeanOption europeanOption = new EuropeanOption();
        assertEquals(EuropeanOptionPricer.class, OptionPricerFactory.getPricer(europeanOption).getClass());
    }
}
