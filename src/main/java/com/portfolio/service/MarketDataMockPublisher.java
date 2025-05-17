package com.portfolio.service;
import com.portfolio.event.StockPriceUpdateEvent;
import com.portfolio.model.instrument.Instrument;
import com.portfolio.model.instrument.InstrumentType;
import com.portfolio.model.instrument.Stock;
import com.portfolio.repository.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableAsync
public class MarketDataMockPublisher {

    private InstrumentRepository instrumentRepository;

    private PortfolioService portfolioService;

    private PricingService pricingService;

    private BlockingQueue<StockPriceUpdateEvent> priceUpdateQueue;

    @Value("${portfolio.stock.aapl.initial.price}")
    private double applInitialPrice;

    @Value("${portfolio.stock.tsla.initial.price}")
    private double tslaInitialPrice;

    private static final double DEFAULT_INITIAL_PRICE = 100;

    private static final SecureRandom random = new SecureRandom();

    private static final Map<String, Double> PRICE_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Stock> STOCK_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        loadStocks();
        initializeDayZeroPrice();
        Thread subscriberThread = new Thread(this::publish, "PublishingThread");
        subscriberThread.start();
    }

    public void publish() {
        System.out.println("Publisher started, publishing to priceUpdateQueue");
        while (!Thread.currentThread().isInterrupted()) {
            try{
                int millsToSleep = random.nextInt(1500) + 500;
                Thread.sleep(millsToSleep); // 0.5-2s
                mockUpdateStockPrice(millsToSleep / 1000.0);
                //publish price to a blocking queue that market data subscriber is listening the price update
                for(Map.Entry<String, Double> updatedStockPrice : PRICE_MAP.entrySet()){
                    priceUpdateQueue.put(new StockPriceUpdateEvent(updatedStockPrice.getKey(), updatedStockPrice.getValue()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
                System.err.println("Exception Occurred. Stopping the publishing");
                Thread.currentThread().interrupt();;
            }
        }
    }

    private void loadStocks(){
        for (Instrument instrument : instrumentRepository.findInstruments(InstrumentType.STOCK)) {
            Stock stock = (Stock) instrument;
            STOCK_MAP.put(stock.getTicker(), stock);
        }
    }

    private void initializeDayZeroPrice(){
        for (String ticker : STOCK_MAP.keySet()) {
            if("TSLA".equals(ticker)){
                PRICE_MAP.put(ticker, tslaInitialPrice);
            }
            else if("AAPL".equals(ticker)){
                PRICE_MAP.put(ticker, applInitialPrice);
            }
            else{
                //default to 100
                PRICE_MAP.put(ticker, DEFAULT_INITIAL_PRICE);
            }
        }
    }

    private void mockUpdateStockPrice(double dT){
        Map<String, Double> newPrices = new HashMap<>();
        for (String ticker : STOCK_MAP.keySet()) {
            double currentPrice = PRICE_MAP.get(ticker);
            double annualizedSd = STOCK_MAP.get(ticker).getAnnualizedSd();
            double expectedReturn = STOCK_MAP.get(ticker).getExpectedReturn();
            double nextPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);
            newPrices.put(ticker, nextPrice);
        }
        PRICE_MAP.putAll(newPrices);
    }

    @Autowired
    @Qualifier("priceUpdateQueue")
    public void setPriceUpdateQueue(BlockingQueue<StockPriceUpdateEvent> priceUpdateQueue){
        this.priceUpdateQueue = priceUpdateQueue;
    }

    @Autowired
    public void setInstrumentRepository(InstrumentRepository instrumentRepository){
        this.instrumentRepository = instrumentRepository;
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService){
        this.portfolioService = portfolioService;
    }

    @Autowired
    public void setPricingService(PricingService pricingService){
        this.pricingService = pricingService;
    }

}
