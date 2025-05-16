Setup and Running

Clone the repository.

Place positions.csv in src/main/resources/ (sample provided).

Run gradle build to build the project.

Run gradle bootRun to start the application.

View portfolio updates in the console.

Sample positions.csv

ticker,shares
AAPL,100
MSFT,50
AAPL_CALL,20
MSFT_PUT,10

Notes


Stock prices update every 0.5–2 seconds.

Option prices use custom calculation with fixed volatility (0.2) and risk-free rate (0.05).

H2 database is in-memory and initialized with sample securities.