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

## Configuration
- Update `application.properties` with initial stock prices (e.g., `portfolio.stock.aapl.initial.price` and `portfolio.stock.tsla.initial.price`).
- Update `application.properties` to customize risk free rate (default to 0.02)
- Modify `position.csv` to adjust portfolio holdings.

## Usage
- The system starts a mock market data publisher that generates random stock price updates every 0.5-2 seconds.
- A subscriber listens to these updates and recalculates portfolio values, printing results to the console.
- Portfolio positions are loaded from `position.csv`, supporting both stocks and European options.
