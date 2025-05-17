package com.portfolio.service;

import com.portfolio.event.StockPriceUpdateEvent;
import com.portfolio.model.instrument.InstrumentType;
import com.portfolio.model.instrument.Stock;
import com.portfolio.repository.InstrumentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MarketDataMockPublisherTest {

    private MarketDataMockPublisher publisher;
    private BlockingQueue<StockPriceUpdateEvent> priceUpdateQueue;
    private InstrumentRepository instrumentRepository;
    private PortfolioService portfolioService;
    private PricingService pricingService;

    @BeforeAll
    public void setUp() {
        // Setup: Initialize mocks and publisher
        publisher = new MarketDataMockPublisher();
        instrumentRepository = Mockito.mock(InstrumentRepository.class);
        portfolioService = Mockito.mock(PortfolioService.class);
        pricingService = Mockito.mock(PricingService.class);
        priceUpdateQueue = new LinkedBlockingQueue<>();
        publisher.setPortfolioService(portfolioService);
        publisher.setPriceUpdateQueue(priceUpdateQueue);
        publisher.setInstrumentRepository(instrumentRepository);
        publisher.setPricingService(pricingService);
    }
    
    @Test
    public void publish_shouldUpdateAndPublishPrice() throws InterruptedException {
        // Setup: Create a stock
        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setInstrumentType(InstrumentType.STOCK);
        stock.setAnnualizedSd(0.5);
        stock.setExpectedReturn(0.2);

        // Mock repository to return the stock
        Mockito.when(instrumentRepository.findInstruments(InstrumentType.STOCK))
                .thenReturn(Collections.singletonList(stock));

        // Mock pricing service to return a price
        Mockito.when(pricingService.generateRandomPrice(Mockito.any(Double.class), Mockito.any(Double.class), Mockito.any(Double.class), Mockito.any(Double.class)))
                .thenReturn((150.0));

        publisher.init();

        // Act: Poll the queue for a price update
        StockPriceUpdateEvent update = priceUpdateQueue.poll(10, TimeUnit.SECONDS);

        // Assert
        assertNotNull(update);
        assertEquals("AAPL", update.getTicker());
        assertEquals((150.0), update.getPrice());
    }
}