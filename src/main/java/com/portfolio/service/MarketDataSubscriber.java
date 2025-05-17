package com.portfolio.service;

import com.portfolio.event.StockPriceUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

@Service
public class MarketDataSubscriber {

    private PortfolioService portfolioService;
    private BlockingQueue<StockPriceUpdateEvent> priceUpdateQueue;

    @PostConstruct
    public void init(){
        Thread subscriberThread = new Thread(this::subscribe, "SubscriberThread");
        subscriberThread.start();
    }

    public void subscribe() {
        System.out.println("Subscriber started, listening to priceUpdateQueue");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                //listen stock price update from the queue
                StockPriceUpdateEvent stockPriceUpdateEvent = priceUpdateQueue.take();
                printMarketDataUpdate(stockPriceUpdateEvent);
                //want to make sure portfolio printed is in order so price message will not confuse
                CountDownLatch portfolioPrintedLatch = new CountDownLatch(1);
                portfolioService.onStockPriceUpdate(stockPriceUpdateEvent, portfolioPrintedLatch);
                portfolioPrintedLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception Occurred. Stopping the subscriber");
                Thread.currentThread().interrupt();;
            }
        }
    }

    private void printMarketDataUpdate(StockPriceUpdateEvent stockPriceUpdateEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Market Price Update ===\n");
        sb.append(String.format("%-5s %-5s %n", "Ticker", "Price"));
        sb.append("---------------------------------------------\n");
        sb.append(String.format("%-5s %-5.2f%n",
                    stockPriceUpdateEvent.getTicker(), stockPriceUpdateEvent.getPrice()));
        sb.append("---------------------------------------------\n");
        System.out.println(sb);
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService){
        this.portfolioService = portfolioService;
    }

    @Autowired
    @Qualifier("priceUpdateQueue")
    public void setPriceUpdateQueue(BlockingQueue<StockPriceUpdateEvent> priceUpdateQueue){
        this.priceUpdateQueue = priceUpdateQueue;
    }
}
