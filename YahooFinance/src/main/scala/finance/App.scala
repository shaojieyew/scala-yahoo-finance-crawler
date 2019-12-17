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


      StockQuery.getAllStockSeeder().foreach(stock=>
      {
        updateStock(stock.symbol)
      })

      /*

      val dbaddress = "localhost:5432 postgres admin Data/Listing/SGX.csv"
      val dbuser = "postgres"
      val password = "admin"
      val input = "Data/Listing/SGX.csv"
       */
    }else{
      throw new Exception("Invalid args")
    }
    //updateStock("1A4.SI")



  }

  def updateStock(symbol:String){
    val stockLastUpdate = StockQuery.getStockLastUpdate(symbol)
    Util.printLog("stockLastUpdate, symbol=%s, update=%s".format(symbol,stockLastUpdate))
   // if(stockLastUpdate.isEmpty || ((Timestamp.valueOf(LocalDateTime.now.minusDays(1)).compareTo(stockLastUpdate.get))==1) ){
    if(stockLastUpdate.isEmpty || ((Timestamp.valueOf(LocalDateTime.now.minusHours(6)).compareTo(stockLastUpdate.get))==1) ){
        println("Processing Stock, symbol=%s".format(symbol))
      val stock = Stock.insertStockDetails(symbol)
      if(stock.nonEmpty){
        StockEstimate.insertStockEstimate(stock.get.symbol)
        StockPriceHistorical.insertStockHistoricalData(stock.get.symbol)
        StockDividendHistorical.insertStockHistoricalData(stock.get.symbol)
        StockFinance.insertStockFinance(stock.get.symbol)
        StockStats.insertStockStats(stock.get.symbol)
      }else{
        Util.printLog("Invalid stock, symbol=%s".format(symbol))
      }
    }
  }
}
