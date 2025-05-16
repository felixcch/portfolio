package com.portfolio.service;
import com.portfolio.model.Holding;
import com.portfolio.model.Position;
import com.portfolio.model.instrument.Instrument;
import com.portfolio.model.instrument.Stock;
import com.portfolio.model.instrument.option.EuropeanOption;
import com.portfolio.repository.InstrumentRepository;
import com.portfolio.util.PortfolioResultPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class PortfolioService {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private PricingService pricingService;

    public void updatePortfolioValue(Map<String, Double> realTimeStockPriceMap,
                                     Map<String, Stock> stockStaticMap,
                                     List<Position> positions)
            throws Exception {

        Map<String, Holding> holdingMap = new HashMap<>();
        double nav = 0;

        for (Position position : positions) {
             String ticker = position.getTicker();
             Instrument instrument = instrumentRepository.findById(position.getTicker()).orElse(null);
             double instrumentPrice = 0;
             if(instrument == null){
                 throw new Exception("Failed to find instrument " + ticker);
             }
             if(instrument instanceof Stock){
                 instrumentPrice = realTimeStockPriceMap.get(position.getTicker());
             }
             else if (instrument instanceof EuropeanOption) {
                 String underlyingName = position.getTicker().split("-")[0];
                 double underlyingPrice = realTimeStockPriceMap.get(underlyingName);
                 instrumentPrice = pricingService.getOptionPrice((EuropeanOption) instrument, underlyingPrice, stockStaticMap.get(underlyingName).getAnnualizedSd());
             }
             double marketValue = getMarketValue(position.getShares(), instrumentPrice);
             Holding holding = new Holding(ticker, position.getShares(), marketValue, instrumentPrice);
             holdingMap.put(ticker, holding);
             nav += marketValue * position.getShares();
        }
        //print output
        PortfolioResultPrinter.print(holdingMap, nav);
    }

    private double getMarketValue(int shares, double price) {
        return shares * price;
    }
}