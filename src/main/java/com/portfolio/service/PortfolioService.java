package com.portfolio.service;
import com.portfolio.model.Position;
import com.portfolio.model.PositionLine;
import com.portfolio.event.PositionUpdateEvent;
import com.portfolio.event.StockPriceUpdateEvent;
import com.portfolio.model.instrument.Instrument;
import com.portfolio.model.instrument.Stock;
import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.repository.InstrumentRepository;
import com.portfolio.util.PositionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Service
public class PortfolioService {

    private InstrumentRepository instrumentRepository;
    private PricingService pricingService;
    private BlockingQueue<PositionUpdateEvent> portfolioUpdateQueue;

    private static final Map<String, Position> HOLDING_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public void preloadHoldings(){
        // load position from file
        List<PositionLine> positionLines = new ArrayList<>();
        try{
            positionLines = PositionLoader.loadPositions();
            for(PositionLine positionLine : positionLines){
                Position position = new Position(positionLine.getTicker(), positionLine.getShares(), 0, 0);
                HOLDING_MAP.put(position.getTicker(), position);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onStockPriceUpdate(StockPriceUpdateEvent stockPriceUpdateEvent, CountDownLatch portfolioPrintedLatch) {
        String stockTicker = stockPriceUpdateEvent.getTicker();
        double updatedStockPrice = stockPriceUpdateEvent.getPrice();
        for (Position position : HOLDING_MAP.values()) {
            position.lock.lock();
            try {
                if(stockTicker.equals(position.getTicker())) {
                    position.setInstrumentPrice(updatedStockPrice);
                    position.setMarketValue(getMarketValue(position.getShares(), updatedStockPrice));
                }
                else {
                    // check holding instrument will be impacted by this stock price change event
                    Instrument instrument = instrumentRepository.findById(position.getTicker()).orElse(null);
                    if (instrument == null) {
                        System.out.println("Holding instrument not found in database : " + position.getTicker());
                        continue;
                    }
                    if (instrument instanceof EuropeanOption) {
                        EuropeanOption europeanOption = (EuropeanOption) instrument;
                        if (stockTicker.equals(europeanOption.getUnderlyingTicker())) {
                            Instrument underlying = instrumentRepository.findById(stockTicker).orElse(null);
                            if (underlying == null) {
                                System.out.println("Stock instrument not found in database : " + stockTicker);
                                continue;
                            }
                            Stock stock = (Stock) underlying;
                            double instrumentPrice = Math.max(0.0, pricingService.priceOption((EuropeanOption) instrument,
                                    updatedStockPrice,
                                    stock.getAnnualizedSd()));
                            position.setInstrumentPrice(instrumentPrice);
                            position.setMarketValue(getMarketValue(position.getShares(), instrumentPrice));
                        }
                   }
                }
            }
            finally {
                position.lock.unlock();
            }
        }
        try{
            //publish portfolio update event
            portfolioUpdateQueue.put(new PositionUpdateEvent(HOLDING_MAP.values(), portfolioPrintedLatch));
        }
        catch (InterruptedException interruptedException){
            interruptedException.printStackTrace();
        }
    }

    private double getMarketValue(int shares, double price) {
        return shares * price;
    }

    @Autowired
    public void setPortfolioUpdateQueue(BlockingQueue<PositionUpdateEvent> portfolioUpdateQueue){
        this.portfolioUpdateQueue = portfolioUpdateQueue;
    }

    @Autowired
    public void setInstrumentRepository(InstrumentRepository instrumentRepository){
        this.instrumentRepository = instrumentRepository;
    }

    @Autowired
    public void setPricingService(PricingService pricingService){
        this.pricingService = pricingService;
    }
}