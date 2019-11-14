package finance
import java.sql.Timestamp
import java.time.LocalDateTime
import finance.model.{Stock, StockDividendHistorical, StockEstimate, StockFinance, StockHistorical, StockPrice, StockPriceHistorical, StockSeeder, StockStats}
import finance.query.{StockEarningEstimateQuery, StockQuery, StockRatingQuery, StockRecommendationQuery, StockSeederQuery}
import finance.scraper.yahoo.{StockAnalysisScraper, StockListScraper, StockScraper}
object App {

  //TODO, standardis upgrade and downgrades , overwhelm, overweight etc...
  val UPDATE_SEEDER = false

  var dbaddress = ""
  var dbuser =""
  var dbpassword = ""
  var print = false

  def main(args: Array[String]): Unit = {
    //updateStockSeeder(StockListScraper.STOCKS_UNDERVALUED_LARGE_CAPS)
    // updateStock("Z74.SI")
    // StockSeeder.updateStockSeeder("Data/Listing/SGX.csv")
    if(args.length > 3){
      dbaddress = args(0)
      dbuser = args(1)
      dbpassword = args(2)
      val input = args(3)

      if(args.contains("print")){
        print = true
      }
      if(args.contains("seed")){
        StockSeeder.updateStockSeeder(input)
      }

      /*

      val dbaddress = "localhost:5432 postgres admin Data/Listing/SGX.csv"
      val dbuser = "postgres"
      val password = "admin"
      val input = "Data/Listing/SGX.csv"
       */
    }else{
      throw new Exception("Invalid args")
    }
    /*
    "jdbc:postgresql://localhost:5432",
    "postgres", "admin"
    location of csv

    */


    StockQuery.getAllStockSeeder().foreach(stock=>
    {
      updateStock(stock.symbol)
    })
  }

  def updateStock(symbol:String){
    val stockLastUpdate = StockQuery.getStockLastUpdate(symbol)
    Util.printLog("stockLastUpdate, symbol=%s, update=%s".format(symbol,stockLastUpdate))
    if(stockLastUpdate.isEmpty || ((Timestamp.valueOf(LocalDateTime.now.minusDays(1)).compareTo(stockLastUpdate.get))==1) ){
      println("Processing Stock, symbol=%s".format(symbol))
      Stock.insertStockDetails(symbol)
      StockEstimate.insertStockEstimate(symbol)
      StockPriceHistorical.insertStockHistoricalData(symbol)
      StockDividendHistorical.insertStockHistoricalData(symbol)
      StockFinance.insertStockFinance(symbol)
      StockStats.insertStockStats(symbol)
    }
  }
}
