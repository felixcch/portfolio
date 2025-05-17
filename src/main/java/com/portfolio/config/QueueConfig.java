package com.portfolio.config;

import com.portfolio.event.PositionUpdateEvent;
import com.portfolio.event.StockPriceUpdateEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {

    @Bean(name = "priceUpdateQueue")
    public BlockingQueue<StockPriceUpdateEvent> priceUpdateQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean(name = "portfolioUpdateQueue")
    public BlockingQueue<PositionUpdateEvent> portfolioUpdateQueue() {
        return new LinkedBlockingQueue<>();
    }
}