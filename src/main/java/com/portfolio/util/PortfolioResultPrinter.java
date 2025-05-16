package com.portfolio.util;

import com.portfolio.model.Holding;

import java.util.Map;

public class PortfolioResultPrinter {

    public static void print(Map<String, Holding> holdingMap, double nav) {
        System.out.println("\n=== Portfolio Update ===");
        System.out.printf("%-30s %-20s %-30s %-30s%n",
                "Ticker", "Shares", "Price", "Market Value");
        System.out.println("---------------------------------------------");

        for (Map.Entry<String, Holding> holdingEntry : holdingMap.entrySet()) {
            String ticker = holdingEntry.getKey();
            Holding holding = holdingEntry.getValue();
            double marketValue = holding.getMarketValue();
            double price = holding.getInstrumentPrice();
            int shares = holding.getShares();
            System.out.printf("%-30s %-20d %-30.2f %-30.2f%n",
                    ticker, shares, price, marketValue);
        }

        System.out.println("---------------------------------------------");
        System.out.printf("Total NAV: %.2f%n", nav);
    }

}