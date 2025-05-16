package com.portfolio.pricer;

import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.model.instrument.option.OptionType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OptionPricerFactoryTest {
    @Test
    void testEuropeanOptionPricer() {
        EuropeanOption europeanOption = new EuropeanOption();
        assertEquals(EuropeanOptionPricer.class, OptionPricerFactory.getPricer(europeanOption).getClass());
    }
}
