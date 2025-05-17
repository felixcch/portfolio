package com.portfolio.model;

import java.util.Objects;

public class PositionLine {

    protected String ticker;
    protected int shares;

    public PositionLine(String ticker, int shares) {
        this.ticker = ticker;
        this.shares = shares;
    }

    public String getTicker() { return ticker; }
    public int getShares() { return shares; }
}