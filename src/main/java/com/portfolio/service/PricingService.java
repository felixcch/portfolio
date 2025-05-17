package com.portfolio.service;

import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.pricer.OptionPricer;
import com.portfolio.pricer.OptionPricerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Service
public class PricingService {
    private static final SecureRandom random = new SecureRandom();

    @Value("${portfolio.risk.free.rate:0.02}")
    private double riskfreeRate;

    public double priceOption(EuropeanOption europeanOption, double underlyingPrice, double underlyingAnnualizedStandardDeviation) {
        OptionPricer optionPricer = OptionPricerFactory.getPricer(europeanOption);
        return optionPricer.price(
                europeanOption.getOptionType(),
                underlyingPrice,
                europeanOption.getStrikePrice(),
                getYTM(LocalDate.now(), europeanOption.getStrikeDate()),
                riskfreeRate,
                underlyingAnnualizedStandardDeviation);
    }

    //Discrete Time Geometric Brownian motion for stock prices implemented according to appendix
    public double generateRandomPrice(double currentPrice,
                                      double annualizedStandardDeviation,
                                      double expectedReturn,
                                      double dT){
        // Geometric Brownian Motion
        double randomVariable = random.nextGaussian();
        double timeFraction = dT / 7257600;
        double dS = currentPrice *
                (
                  (expectedReturn * timeFraction)
                  +
                  (annualizedStandardDeviation * randomVariable * Math.sqrt(timeFraction))
                );
        double newPrice = currentPrice + dS;
        //reset to 0.01 if new price is <= 0
        if(newPrice <= 0){
            return 0.01;
        }
        return newPrice;
    }

    private double getYTM(LocalDate d1, LocalDate d2){
        long daysBetween = ChronoUnit.DAYS.between(d1, d2);
        return daysBetween / 365.0;
    }
}
