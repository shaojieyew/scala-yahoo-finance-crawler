package finance.model


import java.sql.Timestamp

import finance.query.{StockDividendQuery, StockPriceQuery}
import finance.scraper.yahoo.StockHistoricalScraper

trait StockHistorical{
  def getLastUpdated() : Option[Timestamp]
  def insertStockHistoricalData(x: StockHistoricalModel): Unit
  def scrap(): List[StockHistoricalModel]
  def scrap(x: Array[Int]): List[StockHistoricalModel]
  var symbol =""
  def insertStockHistoricalData(stock: String): Unit ={
    symbol= stock
    val last_update = getLastUpdated()
    if( last_update.isEmpty){
      scrap().foreach(x=>{
        insertStockHistoricalData(x)
      })
    }else {
      val timestamp = last_update.get.asInstanceOf[Timestamp]
      val start_date = timestamp.toLocalDateTime.plusDays(1)
      scrap(Array(start_date.getYear, start_date.getMonthValue, start_date.getDayOfMonth)).foreach(x=>{
        insertStockHistoricalData(x)
      })
    }
  }
}

object StockPriceHistorical extends StockHistorical {
  override def getLastUpdated(): Option[Timestamp] = {
    StockPriceQuery.getStockPriceLastInsertDate(symbol)
  }

  override def insertStockHistoricalData(x: StockHistoricalModel): Unit = {
    StockPriceQuery.insertStockPrice(x.asInstanceOf[StockPrice])
  }

  override def scrap(): List[StockHistoricalModel] = {
    val yahooHistorical = new StockHistoricalScraper(symbol)
    yahooHistorical.getHistorical()
  }

  override def scrap(start_date: Array[Int]): List[StockHistoricalModel] = {
    val yahooHistorical = new StockHistoricalScraper(symbol,start_date)
    yahooHistorical.getHistorical()
  }
}
object StockDividendHistorical extends StockHistorical {
  override def getLastUpdated(): Option[Timestamp] = {
    StockDividendQuery.getStockDividendLastInsertDate(symbol)
  }

  override def insertStockHistoricalData(x: StockHistoricalModel): Unit = {
    StockDividendQuery.insertStockDividend(x.asInstanceOf[StockDividend])
  }

  override def scrap(): List[StockHistoricalModel] = {
    val yahooHistorical = new StockHistoricalScraper(symbol)
    yahooHistorical.getDividends()
  }

  override def scrap(start_date: Array[Int]): List[StockHistoricalModel] = {
    val yahooHistorical = new StockHistoricalScraper(symbol,start_date)
    yahooHistorical.getDividends()
  }
}
abstract class StockHistoricalModel
case class StockPrice(stock: String, open: Float,
                      high: Float, low: Float, close: Float,
                      adj_close: Float, vol: Long, date: Timestamp) extends StockHistoricalModel
case class StockDividend(stock: String, dividend: Float, date: Timestamp) extends StockHistoricalModel
