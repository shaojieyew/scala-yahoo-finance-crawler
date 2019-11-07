package finance.model

import java.sql.Timestamp

case class StockRecommendation(stock: String,
strong_buy: Int,
buy: Int,
hold: Int,
sell: Int,
strong_sell: Int,
recommended_timestamp: Timestamp){

}
