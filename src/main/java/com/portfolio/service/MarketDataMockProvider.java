package com.portfolio.service;
import com.portfolio.model.Position;
import com.portfolio.model.instrument.Instrument;
import com.portfolio.model.instrument.InstrumentType;
import com.portfolio.model.instrument.Stock;
import com.portfolio.repository.InstrumentRepository;
import com.portfolio.util.PositionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.*;

@Service
public class MarketDataMockProvider {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PricingService pricingService;

    @Value("${portfolio.stock.aapl.initial.price}")
    private double applInitialPrice;

    @Value("${portfolio.stock.tsla.initial.price}")
    private double tslaInitialPrice;

    private static final SecureRandom random = new SecureRandom();

    @PostConstruct
    public void start() {
        // load stocks
        Map<String, Stock> stockStaticMap = loadStocks();
        // initialize day 0 price
        Map<String, Double> realTimeStockPriceMap = initializeDayZeroPrice(stockStaticMap.keySet());
        // load position
        List<Position> positions = new ArrayList<>();
        try{
             positions = PositionLoader.loadPositions();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to load position. Stopping the application");
            return;
        }
        // start the loop
        while (true) {
            try{
                portfolioService.updatePortfolioValue(realTimeStockPriceMap, stockStaticMap, positions);
                int millsToSleep = random.nextInt(1500) + 500;
                Thread.sleep(millsToSleep); // 0.5-2s
                mockUpdateStockPrice(realTimeStockPriceMap, stockStaticMap, millsToSleep / 1000.0);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Failed to loop. Stopping the application");
                break;
            }
        }
    }

    private Map<String, Stock> loadStocks(){
        Map<String, Stock> stockStaticMap = new HashMap<>();
        for (Instrument instrument : instrumentRepository.findInstruments(InstrumentType.STOCK)) {
            Stock stock = (Stock) instrument;
            stockStaticMap.put(stock.getTicker(), stock);
        }
        return stockStaticMap;
    }

    private Map<String, Double> initializeDayZeroPrice(Set<String> tickers){
        Map<String, Double> realTimeStockPriceMap = new HashMap<>();
        for (String ticker : tickers) {
            if("TSLA".equals(ticker)){
                realTimeStockPriceMap.put(ticker, tslaInitialPrice);
            }
            else if("AAPL".equals(ticker)){
                realTimeStockPriceMap.put(ticker, applInitialPrice);
            }
            else{
                realTimeStockPriceMap.put(ticker, 100.0);
            }
        }
        return realTimeStockPriceMap;
    }


    private void mockUpdateStockPrice(Map<String, Double> realTimeStockPriceMap,
                                      Map<String, Stock> stockStaticMap,
                                      double dT){
        for (String ticker : realTimeStockPriceMap.keySet()) {
            double currentPrice = realTimeStockPriceMap.get(ticker);
            double annualizedSd = stockStaticMap.get(ticker).getAnnualizedSd();
            double expectedReturn = stockStaticMap.get(ticker).getExpectedReturn();
            double nextPrice = pricingService.generateRandomPrice(currentPrice, annualizedSd, expectedReturn, dT);
            realTimeStockPriceMap.put(ticker, nextPrice);
        }
    }
}
