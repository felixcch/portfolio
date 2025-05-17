package com.portfolio.model.instrument;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("STOCK")
public class Stock extends Instrument {

    private Double annualizedSd;
    private Double expectedReturn;
    public Double getAnnualizedSd() { return annualizedSd; }
    public Double getExpectedReturn() { return expectedReturn; }
    public void setAnnualizedSd(Double annualizedSd) { this.annualizedSd = annualizedSd; }
    public void setExpectedReturn(Double expectedReturn) { this.expectedReturn = expectedReturn; }
}

