package com.portfolio.model;

public class Position {

    private String ticker;
    private int shares;

    public Position(String ticker, int shares) {
        this.ticker = ticker;
        this.shares = shares;
    }

    public String getTicker() { return ticker; }
    public int getShares() { return shares; }

}