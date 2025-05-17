package com.portfolio.model;

import java.util.concurrent.locks.ReentrantLock;

public class Position extends PositionLine {
    private double marketValue;
    private double instrumentPrice;
    public final ReentrantLock lock = new ReentrantLock();
    public Position(String ticker, int shares, double marketValue, double instrumentPrice) {
        super(ticker, shares);
        this.marketValue = marketValue;
        this.instrumentPrice = instrumentPrice;
    }
    public double getMarketValue() { lock.lock();
        try {
            return marketValue;
        } finally {
            lock.unlock();
        }
    }
    public double getInstrumentPrice() {
        lock.lock();
        try {
            return instrumentPrice;
        } finally {
            lock.unlock();
        } }
    public void setInstrumentPrice(double instrumentPrice) {
        lock.lock();
        try {
            this.instrumentPrice = instrumentPrice;
        } finally {
            lock.unlock();
        } }
    public void setMarketValue(double marketValue) {
        lock.lock();
        try {
            this.marketValue = marketValue;
        } finally {
            lock.unlock();
        }
    }
    public Position copy() {
        lock.lock();
        try {
            return new Position(ticker, shares, instrumentPrice, marketValue);
        } finally {
            lock.unlock();
        }
    }
}
