package finance.model

import java.sql.Timestamp

case class StockRecommendation(stock: String,
strong_buy: Option[Int],
buy:  Option[Int],
hold:  Option[Int],
sell:  Option[Int],
strong_sell:  Option[Int],
recommended_timestamp: Timestamp){

}
