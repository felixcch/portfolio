# Portfolio Management System

## Overview
This is a Java-based small scale portfolio management system designed to simulate and manage stock and option portfolios. The system includes features for real-time stock price updates, portfolio valuation, and option pricing using Monte Carlo methods and Black-Scholes models.

## Features
- Real-time stock price simulation using Geometric Brownian Motion.
- Portfolio valuation with support for stocks and European options.
- Market data publishing and subscription mechanism using blocking queues.
- Option pricing with customizable risk-free rates and volatility inputs.
- Loading and managing portfolio positions from CSV files.

## Project Structure
- `src/main/java/com.portfolio`: Contains the core logic and services.
  - `config`: Configuration classes for queue and event handling.
  - `event`: Event classes for stock price and position updates.
  - `model`: Data models for instruments (stocks, options), positions, etc.
  - `pricer`: Option pricing implementations.
  - `repository`: Data access layer for instruments.
  - `service`: Core services for market data, portfolio management, and pricing.
  - `util`: Utility classes for mathematical computations.
- `src/test/java/com.portfolio`: Unit tests for the system components.
- `resources`: Configuration files (`application.properties`) and data files (`position.csv`).

## Installation
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd portfolio
   ```
3. Build the project using Gradle:
   ```
   ./gradlew build
   ```
4. Run the application:
   ```
   ./gradlew run
   ```
## Example Output
Below is a sample output from the system showing a market price update followed by the portfolio valuation:

```
=== Market Price Update ===
Ticker Price 
---------------------------------------------
TSLA  349.56
---------------------------------------------


=== Portfolio ===
Ticker                         Shares               Price                          Market Value                  
---------------------------------------------
AAPL                           1000                 207.04                         207035.88                     
TSLA                           -500                 349.56                         -174779.17                    
AAPL-OCT-2025-110-P            20000                3.95                           79061.38                      
AAPL-OCT-2025-300-C            -20000               18.03                          -360513.36                    
TSLA-DEC-2025-400-P            -10000               90.73                          -907255.17                    
TSLA-NOV-2025-400-C            10000                39.56                          395570.90                     
---------------------------------------------
Total NAV: -760879.54
```


## Configuration
- Update `application.properties` with initial stock prices (e.g., `portfolio.stock.aapl.initial.price` and `portfolio.stock.tsla.initial.price`).
- Update `application.properties` to customize risk free rate (default to 0.02)
- Modify `position.csv` to adjust portfolio holdings.

## Usage
- The system starts a mock market data publisher that generates random stock price updates every 0.5-2 seconds.
- A subscriber listens to these updates and recalculates portfolio values, printing results to the console.
- Portfolio positions are loaded from `position.csv`, supporting both stocks and European options.
