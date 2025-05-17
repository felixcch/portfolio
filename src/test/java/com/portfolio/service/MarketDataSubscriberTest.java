package com.portfolio.service;

import com.portfolio.event.StockPriceUpdateEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;


import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MarketDataSubscriberTest {

    private MarketDataSubscriber subscriber;
    private PortfolioService portfolioService;
    private LinkedBlockingQueue<StockPriceUpdateEvent> priceUpdateQueue;

    @BeforeAll
    public void setUp() {
        subscriber = new MarketDataSubscriber();
        portfolioService = Mockito.mock(PortfolioService.class);
        priceUpdateQueue = new LinkedBlockingQueue<>();
        subscriber.setPortfolioService(portfolioService);
        subscriber.setPriceUpdateQueue(priceUpdateQueue);
    }

    @Test
    public void subscribe_shouldProcessPriceUpdate() throws InterruptedException {
        // Setup: Add a price update to the queue
        StockPriceUpdateEvent update = new StockPriceUpdateEvent("AAPL",150.0);
        priceUpdateQueue.put(update);

        // Act: Start the subscriber
        subscriber.init();

        // Wait briefly to allow the subscriber thread to process
        Thread.sleep(100);

        // Assert: Verify portfolioService was called
        verify(portfolioService).onStockPriceUpdate(any(), any());

        // Stop the subscriber thread
        Thread.currentThread().interrupt();
    }
}