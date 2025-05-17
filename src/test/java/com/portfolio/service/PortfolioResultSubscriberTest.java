package com.portfolio.service;

import com.portfolio.model.Position;
import com.portfolio.event.PositionUpdateEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PortfolioResultSubscriberTest {

    private PortfolioResultSubscriber subscriber;
    private LinkedBlockingQueue<PositionUpdateEvent> portfolioUpdateQueue;

    @BeforeAll
    public void setUp() {
        subscriber = new PortfolioResultSubscriber();
        portfolioUpdateQueue = new LinkedBlockingQueue<>();
        subscriber.setPortfolioUpdateQueue(portfolioUpdateQueue);
    }

    @Test
    public void subscribe_shouldProcessPortfolioUpdate() throws InterruptedException {
        // Setup: Create a portfolio update
        Position position = new Position("AAPL", 100, 150.0, 15000.0);
        Collection<Position> positions = Arrays.asList(position);
        portfolioUpdateQueue.put(new PositionUpdateEvent(positions, new CountDownLatch(1)));

        // Act: Start the subscriber
        subscriber.init();

        // Wait briefly to allow the subscriber thread to process
        Thread.sleep(100);

        // Assert: Verify the queue is empty (update was consumed)
        assertTrue(portfolioUpdateQueue.isEmpty());

        // Stop the subscriber thread
        Thread.currentThread().interrupt();
    }
}