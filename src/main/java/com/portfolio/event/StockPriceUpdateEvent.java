package com.portfolio.event;

public class StockPriceUpdateEvent {
    private final String ticker;
    private final double price;

    public StockPriceUpdateEvent(String ticker, double price) {
        this.ticker = ticker;
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }
}