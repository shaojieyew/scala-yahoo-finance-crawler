package finance.model

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}

import finance.Util
import finance.query.{StockDividendQuery, StockEarningEstimateQuery, StockPriceQuery, StockRevenueEstimateQuery}
import finance.scraper.yahoo.StockHistoricalScraper

case class Stock(symbol: String,
                 name: Option[String],
                 industry: Option[String],
                 sector: Option[String],
                 country: Option[String],
                 market: Option[String],
                 exchange: Option[String],
                 website: Option[String],
                 description: Option[String],
                 quote_type: Option[String],
                 exchange_timezone_name: Option[String],
                 is_esg_populated: Option[Boolean],
                 is_tradeable :Option[Boolean],
                 full_time_employees: Option[Int]){

  var ratings: List[StockRating] = List()
  var recommendations: List[StockRecommendation] = List()

}

