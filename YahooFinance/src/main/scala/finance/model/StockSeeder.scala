package finance.model

import java.sql.Timestamp

import finance.Util
import finance.query.StockSeederQuery
import finance.scraper.yahoo.StockListScraper

case class StockSeeder(symbol: String, src: String
                       , created_timestamp: Timestamp
                       , updated_timestamp: Timestamp)

object StockSeeder{

  //val STOCKS_SG = "https://sg.finance.yahoo.com/screener/unsaved/9acb0a49-73de-425c-abe3-5a50943d8418"
  //val STOCKS_US = "https://sg.finance.yahoo.com/screener/unsaved/a0c769a9-36ee-4e52-948e-49983db91cfc"
  //val STOCKS_SG_US = "  https://sg.finance.yahoo.com/screener/unsaved/a06bc07d-799c-4a99-b9bc-6c90c23d1776"
  val STOCKS_UNDERVALUED_LARGE_CAPS = "https://sg.finance.yahoo.com/screener/predefined/undervalued_large_caps"
  val STOCKS_SG_MID_LARGE_MEGA_CAPS = "https://sg.finance.yahoo.com/screener/350511a6-ba9b-40cd-8814-1e1c807ad371"


  def updateStockSeeder(url:String): Unit ={
    Util.printLog("updateStockSeeder")
    try{
      val src = "yahoo"
      val symbols = StockListScraper.get(url)
      symbols.map(symbol=> {
        StockSeederQuery.insertStockSeeder(symbol,src)
      }
      )
    }
    catch{
      case _: Exception => Util.printLog("updateStockSeeder, Got some exception")
    }
  }

}