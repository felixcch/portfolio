package com.portfolio.pricer;

import com.portfolio.model.instrument.option.OptionType;
import com.portfolio.util.MathUtil;

public class EuropeanOptionPricer implements OptionPricer{

    public double price(OptionType type,
                        double underlyingPrice,
                        double strikePrice,
                        double yearToMaturity,
                        double riskFreeRate,
                        double sigma){
        if (type == null) {
            throw new IllegalArgumentException("Option type cannot be null");
        }
        if (type != OptionType.CALL && type != OptionType.PUT) {
            throw new IllegalArgumentException("Invalid option type: " + type);
        }
        if (underlyingPrice <= 0) {
            throw new IllegalArgumentException("Underlying price must be positive: " + underlyingPrice);
        }
        if (strikePrice <= 0) {
            throw new IllegalArgumentException("Strike price must be positive: " + strikePrice);
        }
        if (sigma <= 0) {
            throw new IllegalArgumentException("Volatility must be positive: " + sigma);
        }

        if (yearToMaturity <= 0) {
            return type == OptionType.CALL ? Math.max(0, underlyingPrice - strikePrice) : Math.max(0, strikePrice - underlyingPrice);
        }

        double d1 = (Math.log(underlyingPrice / strikePrice) + (riskFreeRate + sigma * sigma / 2) * yearToMaturity) / (sigma * Math.sqrt(yearToMaturity));
        double d2 = d1 - sigma * Math.sqrt(yearToMaturity);

        double normD1 = MathUtil.normCdf(d1);
        double normD2 = MathUtil.normCdf(d2);

        if (type == OptionType.CALL) {
            return underlyingPrice * normD1 - strikePrice * Math.exp(-riskFreeRate * yearToMaturity) * normD2;
        } else {
            return strikePrice * Math.exp(-riskFreeRate * yearToMaturity) * (1 - normD2) - underlyingPrice * (1 - normD1);
        }
    }
}
