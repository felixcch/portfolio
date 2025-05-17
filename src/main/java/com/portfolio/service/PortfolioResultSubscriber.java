package com.portfolio.service;

import com.portfolio.model.Position;
import com.portfolio.event.PositionUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

@Service
public class PortfolioResultSubscriber {

    private BlockingQueue<PositionUpdateEvent> portfolioUpdateQueue;

    @PostConstruct
    public void init(){
        Thread subscriberThread = new Thread(this::subscribe, "PortfolioResultSubscriberThread");
        subscriberThread.start();
    }

    public void subscribe() {
        System.out.println("Portfolio result Subscriber started, listening to positionUpdateQueue");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                PositionUpdateEvent positionUpdate = portfolioUpdateQueue.take();
                printPortfolio(positionUpdate.getPositions());
                positionUpdate.getLatch().countDown();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception Occurred. Stopping the subscriber");
                Thread.currentThread().interrupt();;
            }
        }
    }

    private void printPortfolio(Collection<Position> positions) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Portfolio ===\n");
        sb.append(String.format("%-30s %-20s %-30s %-30s%n",
                "Ticker", "Shares", "Price", "Market Value"));
        sb.append("---------------------------------------------\n");

        for (Position position : positions) {
            String ticker = position.getTicker();
            double marketValue = position.getMarketValue();
            double price = position.getInstrumentPrice();
            int shares = position.getShares();
            sb.append(String.format("%-30s %-20d %-30.2f %-30.2f%n",
                    ticker, shares, price, marketValue));
        }

        sb.append("---------------------------------------------\n");
        sb.append(String.format("Total NAV: %.2f%n\n", getNav(positions)));
        System.out.println(sb);
    }

    private double getNav(Collection<Position> positions){
        double nav = 0;
        for(Position position : positions){
            nav += position.getMarketValue();
        }
        return nav;
    }
    @Autowired
    @Qualifier("portfolioUpdateQueue")
    public void setPortfolioUpdateQueue(BlockingQueue<PositionUpdateEvent> portfolioUpdateQueue){
        this.portfolioUpdateQueue = portfolioUpdateQueue;
    }


}
