package finance.model

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}

import finance.Util
import finance.query.{StockEarningEstimateQuery, StockPriceQuery, StockRevenueEstimateQuery}
import finance.scraper.yahoo.StockHistoricalScraper

case class Stock(symbol: String,
                 name: String,
                 industry: String,
                 sector: String,
                 country: String,
                 market: String,
                 exchange: String,
                 website: String,
                 description: String,
                 quote_type: String,
                 exchange_timezone_name: String,
                 is_esg_populated: Boolean,
                 is_tradeable :Boolean,
                 full_time_employees: Int){

  var ratings: List[StockRating] = List()
  var recommendations: List[StockRecommendation] = List()

}


trait StockHistoricalData{
  def getLastUpdated() : Option[Timestamp]
  def insertStock(x: HistoricalData): Unit
  def scrap(): List[HistoricalData]
  def scrap(x: Array[Int]): List[HistoricalData]
  def insertStockHistoricalData(stock: String)

  def insertStockHistoricalData(): Unit ={
    //change this
    val last_update = getLastUpdated()
    if( last_update.isEmpty){
      scrap().foreach(x=>{
        //change this
        insertStock(x)
      })
    }else {
      val timestamp = last_update.get.asInstanceOf[Timestamp]
      val start_date = timestamp.toLocalDateTime.plusDays(1)
      scrap(Array(start_date.getYear, start_date.getMonthValue, start_date.getDayOfMonth)).foreach(x=>{
        //change this
        insertStock(x)
      })
    }
  }
}

object StockPriceHistoricalData extends StockHistoricalData {
  var symbol: String =""
  def insertStockHistoricalData(stock:String): Unit ={
    symbol=stock
    super.insertStockHistoricalData()
  }
  override def getLastUpdated(): Option[Timestamp] = {
    StockPriceQuery.getStockPriceLastInsertDate(symbol)
  }

  override def insertStock(x: HistoricalData): Unit = {
    StockPriceQuery.insertStockPrice(x.asInstanceOf[StockPrice])
  }

  override def scrap(): List[HistoricalData] = {
    val yahooHistorical = new StockHistoricalScraper(symbol)
    yahooHistorical.getHistorical()
  }

  override def scrap(start_date: Array[Int]): List[HistoricalData] = {
    val yahooHistorical = new StockHistoricalScraper(symbol,start_date)
    yahooHistorical.getHistorical()
  }
}

trait HistoricalData
case class StockPrice(stock: String, open: Float,
                      high: Float, low: Float, close: Float,
                      adj_close: Float, vol: Long, date: Timestamp) extends HistoricalData


trait StockAnalysisEstimate{
  def insertStockEstimateQuery()
}


case class StockRevenueEstimate(stock: String,est_date: Timestamp,
                                period: String, end_date: Timestamp,
                                est_avg: Long, est_low: Long, est_high: Long,
                                no_analyst: Int, growth: Float) extends StockAnalysisEstimate{
  def insertStockEstimateQuery(): Unit ={
    StockRevenueEstimateQuery.insertStockRevenueEstimateQuery(this)
  }
}

case class StockEarningEstimate(stock: String, est_date: Timestamp,
                                period: String, end_date: Timestamp,
                                est_avg: Float, est_low: Float, est_high: Float,
                                no_analyst: Int, growth: Float) extends StockAnalysisEstimate{
  def insertStockEstimateQuery(): Unit ={
    StockEarningEstimateQuery.insertStockEarningEstimateQuery(this)
  }
}