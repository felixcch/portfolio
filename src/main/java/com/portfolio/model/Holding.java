package com.portfolio.model;

public class Holding extends Position {
    private final double marketValue;
    private final double instrumentPrice;
    public Holding(String ticker, int shares, double marketValue, double instrumentPrice) {
        super(ticker, shares);
        this.marketValue = marketValue;
        this.instrumentPrice = instrumentPrice;
    }
    public double getMarketValue() { return marketValue; }
    public double getInstrumentPrice() { return instrumentPrice; }
}
