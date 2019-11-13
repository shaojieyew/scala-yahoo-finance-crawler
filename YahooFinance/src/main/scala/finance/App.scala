package finance
import finance.model.{Stock, StockDividendHistorical, StockEstimate, StockFinance, StockHistorical, StockPrice, StockPriceHistorical, StockSeeder, StockStats}
import finance.query.{StockEarningEstimateQuery, StockQuery, StockRatingQuery, StockRecommendationQuery, StockSeederQuery}
import finance.scraper.yahoo.{StockAnalysisScraper, StockListScraper, StockScraper}
object App {

  //TODO, standardis upgrade and downgrades , overwhelm, overweight etc...
  val UPDATE_SEEDER = false

  def main(args: Array[String]): Unit = {
    //updateStockSeeder(StockListScraper.STOCKS_UNDERVALUED_LARGE_CAPS)
    // updateStock("Z74.SI")
    // StockSeeder.updateStockSeeder("Data/Listing/SGX.csv")
    StockQuery.getAllStockSeeder().foreach(stock=>
    {
      updateStock(stock.symbol)
    })
  }


  def updateStock(symbol:String){
    Stock.insertStockDetails(symbol)
    StockEstimate.insertStockEstimate(symbol)
    StockPriceHistorical.insertStockHistoricalData(symbol)
    StockDividendHistorical.insertStockHistoricalData(symbol)
    StockFinance.insertStockFinance(symbol)
    StockStats.insertStockStats(symbol)
  }
}
