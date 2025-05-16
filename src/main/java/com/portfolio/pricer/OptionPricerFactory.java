package com.portfolio.pricer;

import com.portfolio.model.instrument.Instrument;
import com.portfolio.model.instrument.option.EuropeanOption;


public class OptionPricerFactory {
    public static OptionPricer getPricer(Instrument instrument){
        if (instrument instanceof EuropeanOption) {
            return new EuropeanOptionPricer();
        }
        throw new IllegalArgumentException("Unsupported instrument type : " + instrument.getInstrumentType());
    }
}
