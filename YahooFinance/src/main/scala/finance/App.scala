package finance
import finance.query.{StockQuery, StockSeederQuery}
import finance.scraper.yahoo.StockListScraper
import finance.scraper.yahoo.StockScraper
object App {

  val UPDATE_SEEDER = false

  def main(args: Array[String]): Unit = {
    //updateStockSeeder(StockListScraper.STOCKS_UNDERVALUED_LARGE_CAPS)
    updateStock("GOOGL")
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
    if(StockQuery.getStock(stock.symbol).isEmpty) StockQuery.insertStock(stock) else StockQuery.updateStock(stock)
  }
}
