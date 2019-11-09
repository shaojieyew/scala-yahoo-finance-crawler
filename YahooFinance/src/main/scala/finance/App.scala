package finance
import finance.model.{StockDividendHistorical, StockFinance, StockHistorical, StockPrice, StockPriceHistorical}
import finance.query.{StockEarningEstimateQuery, StockQuery, StockRatingQuery, StockRecommendationQuery, StockSeederQuery}
import finance.scraper.yahoo.{StockAnalysisScraper, StockListScraper, StockScraper}
object App {

  //TODO, include summary stats of te stocks
  //TODO, analyst target price summary
  //TODO, standardis upgrade and downgrades , overwhelm, overweight etc...
  val UPDATE_SEEDER = false

  def main(args: Array[String]): Unit = {
    //updateStockSeeder(StockListScraper.STOCKS_UNDERVALUED_LARGE_CAPS)
    updateStock("C6L.SI")
  }

  def updateStockSeeder(url:String): Unit ={
    Util.printLog("updateStockSeeder")

    try{
      val src = "yahoo"
      val symbols = StockListScraper.get(url)
      symbols.map(symbol=> {
        if (StockSeederQuery.getStockSeeder(symbol,src).nonEmpty){
          StockSeederQuery.updateStockSeeder(symbol,src)
        } else{
          StockSeederQuery.insertStockSeeder(symbol,src)
        }
      }
      )
    }
    catch{
      case _: Exception => Util.printLog("updateStockSeeder, Got some exception")
    }
  }


  def updateStock(symbol:String){
    val stock = StockScraper.get(symbol)
    StockQuery.insertUpdateStock(stock)
    stock.ratings.foreach(x => StockRatingQuery.insertStockRating(x))
    stock.recommendations.foreach(x => StockRecommendationQuery.insertStockRecommendation(x))

    val estimate_earnings_rev = StockAnalysisScraper.get(symbol)
    estimate_earnings_rev.foreach(x => x.insertStockEstimateQuery())

    StockPriceHistorical.insertStockHistoricalData(symbol)
    StockDividendHistorical.insertStockHistoricalData(symbol)
    StockFinance.insertUpdateStockFinance(symbol)
  }
}
