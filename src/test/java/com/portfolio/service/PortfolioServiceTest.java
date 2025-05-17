package com.portfolio.service;

import com.portfolio.model.Position;
import com.portfolio.event.PositionUpdateEvent;
import com.portfolio.event.StockPriceUpdateEvent;
import com.portfolio.model.instrument.InstrumentType;
import com.portfolio.model.instrument.Stock;
import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.model.instrument.option.OptionType;
import com.portfolio.repository.InstrumentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PortfolioServiceTest {

    private PortfolioService portfolioService;
    private InstrumentRepository instrumentRepository;
    private PricingService pricingService;
    private LinkedBlockingQueue<PositionUpdateEvent> portfolioUpdateQueue;

    @BeforeAll
    public void setUp() {
        portfolioService = new PortfolioService();
        instrumentRepository = mock(InstrumentRepository.class);
        pricingService = mock(PricingService.class);
        portfolioUpdateQueue = new LinkedBlockingQueue<>();
        portfolioService.setInstrumentRepository(instrumentRepository);
        portfolioService.setPricingService(pricingService);
        portfolioService.setPortfolioUpdateQueue(portfolioUpdateQueue);
        portfolioService.preloadHoldings();
    }

    @Test
    public void onStockPriceUpdate_shouldUpdateStockPosition() {

        // Mock repository
        Stock stock = new Stock();
        stock.setTicker("AAPL");
        when(instrumentRepository.findById("AAPL")).thenReturn(Optional.of(stock));

        StockPriceUpdateEvent update = new StockPriceUpdateEvent("AAPL", 150.0);
        portfolioService.onStockPriceUpdate(update, new CountDownLatch(1));

        // Assert
        assertFalse(portfolioUpdateQueue.isEmpty());
        Collection<Position> positions = portfolioUpdateQueue.poll().getPositions();
        assertNotNull(positions);
        Optional<Position> updatedPosition = positions.stream().filter(position -> position.getTicker().equals("AAPL")).findFirst();
        assertTrue(updatedPosition.isPresent());
        assertEquals("AAPL", updatedPosition.get().getTicker());
        assertEquals(150.0, updatedPosition.get().getInstrumentPrice(), 0.01);
        assertEquals(150000.0, updatedPosition.get().getMarketValue(), 0.01);
    }

    @Test
    public void onStockPriceUpdate_shouldUpdateOptionPosition() {

        // Mock repository
        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setAnnualizedSd(0.5);
        stock.setExpectedReturn(0.5);
        when(instrumentRepository.findById("AAPL")).thenReturn(Optional.of(stock));

        EuropeanOption europeanOption = new EuropeanOption();
        europeanOption.setTicker("AAPL-OCT-2025-300-C");
        europeanOption.setOptionType(OptionType.CALL);
        europeanOption.setStrikeDate(LocalDate.of(2025, 10,1));
        europeanOption.setStrikePrice(200.0);
        europeanOption.setInstrumentType(InstrumentType.EUROPEAN_OPTION);
        europeanOption.setUnderlyingTicker("AAPL");
        when(instrumentRepository.findById("AAPL")).thenReturn(Optional.of(stock));
        when(instrumentRepository.findById("AAPL-OCT-2025-300-C")).thenReturn(Optional.of(europeanOption));
        when(pricingService.priceOption(europeanOption, 150.0, 0.5)).thenReturn(123.0);
        StockPriceUpdateEvent update = new StockPriceUpdateEvent("AAPL", 150.0);
        portfolioService.onStockPriceUpdate(update, new CountDownLatch(1));

        // Assert
        assertFalse(portfolioUpdateQueue.isEmpty());
        Collection<Position> positions = portfolioUpdateQueue.poll().getPositions();
        assertNotNull(positions);
        Optional<Position> updatedPosition = positions.stream().filter(position -> position.getTicker().equals("AAPL-OCT-2025-300-C")).findFirst();
        assertTrue(updatedPosition.isPresent());
        assertEquals(123.0, updatedPosition.get().getInstrumentPrice(), 0.01);
        assertEquals(123.0 * -20000, updatedPosition.get().getMarketValue(), 0.01);
    }
}