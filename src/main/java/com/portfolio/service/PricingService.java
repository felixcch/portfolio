package com.portfolio.service;

import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.pricer.OptionPricer;
import com.portfolio.pricer.OptionPricerFactory;
import org.h2.util.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Service
public class PricingService {
    private static final SecureRandom random = new SecureRandom();

    public double getOptionPrice(EuropeanOption europeanOption, double underlyingPrice, double underlyingAnnualizedStandardDeviation) {
        OptionPricer optionPricer = OptionPricerFactory.getPricer(europeanOption);
        return optionPricer.price(
                europeanOption.getOptionType(),
                underlyingPrice,
                europeanOption.getStrikePrice(),
                getYTM(LocalDate.now(), europeanOption.getStrikeDate()),
                0.02,
                underlyingAnnualizedStandardDeviation);
    }

    //Discrete Time Geometric Brownian motion for stock prices implemented according to appendix
    public double generateRandomPrice(double currentPrice,
                                      double annualizedStandardDeviation,
                                      double expectedReturn,
                                      double dT){
        // Geometric Brownian Motion
        double randomVariable = random.nextGaussian();
        double dS = currentPrice *
                (
                  (expectedReturn * dT / 7257600)
                  +
                  (annualizedStandardDeviation * randomVariable * Math.sqrt(dT / 7257600))
                );
        return currentPrice + dS;
    }

    private double getYTM(LocalDate d1, LocalDate d2){
        long daysBetween = ChronoUnit.DAYS.between(d1, d2);
        return daysBetween / 365.0;
    }
}
