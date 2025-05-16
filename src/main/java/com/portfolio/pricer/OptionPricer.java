package com.portfolio.pricer;

import com.portfolio.model.instrument.option.OptionType;
import com.portfolio.util.MathUtil;

public interface OptionPricer {

    double price(OptionType type,
                        double underlyingPrice,
                        double strikePrice,
                        double timeToMaturity,
                        double riskFreeRate,
                        double sigma);
}
