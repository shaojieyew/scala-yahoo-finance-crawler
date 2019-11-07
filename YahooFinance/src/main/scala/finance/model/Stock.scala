package finance.model

import java.sql.Timestamp

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

case class StockEarningEstimate(stock: String, est_date: Timestamp, period: String, end_date: Timestamp, est_avg: Float, est_low: Float, est_high: Float, no_analyst: Int, growth: Float)