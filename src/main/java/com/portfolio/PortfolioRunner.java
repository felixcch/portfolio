package com.portfolio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.portfolio.repository")
public class PortfolioRunner {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioRunner.class, args);
    }

}