package finance.query

import doobie.implicits._
import finance.Util
import finance.model._

object StockRecommendationQuery extends Database{


  def getStockRecommendation(symbol: String): List[StockRecommendation] ={
    Util.printLog("StockRecommendationQuery getStockRecommendation, symbol=%s".format(symbol))
    sql"""
        SELECT stock, strong_buy, buy, hold, sell, strong_sell, recommended_timestamp
        FROM finance.stock_recommendation
        where stock = $symbol
        """
      .query[StockRecommendation]
      .nel
      .transact(xa)
      .unsafeRunSync.toList
  }

  def getStockRecommendation(stockRecommendation: StockRecommendation): Option[StockRecommendation] ={
    Util.printLog("StockRecommendationQuery getStockRecommendation, symbol=%s, recommended_timestamp=%s".format(stockRecommendation.stock, stockRecommendation.recommended_timestamp))
    sql"""
        select stock, strong_buy, buy, hold, sell, strong_sell, recommended_timestamp from finance.stock_recommendation
        where stock = ${stockRecommendation.stock}
                       and strong_buy = ${stockRecommendation.strong_buy}
                       and buy = ${stockRecommendation.buy}
                       and hold = ${stockRecommendation.hold}
                       and sell = ${stockRecommendation.sell}
                       and strong_sell = ${stockRecommendation.strong_sell}
                       and recommended_timestamp = ${stockRecommendation.recommended_timestamp}
        """
      .query[StockRecommendation]
      .option
      .transact(xa)
      .unsafeRunSync
  }


  def deleteStockRecommendation(stockRecommendation: StockRecommendation): Unit ={
    Util.printLog("StockRecommendationQuery deleteStockRecommendation, symbol=%s, recommended_timestamp=%s".format(stockRecommendation.stock, stockRecommendation.recommended_timestamp))
    sql"""
        delete from finance.stock_recommendation
        where stock = ${stockRecommendation.stock}
                       and strong_buy = ${stockRecommendation.strong_buy}
                       and buy = ${stockRecommendation.buy}
                       and hold = ${stockRecommendation.hold}
                       and sell = ${stockRecommendation.sell}
                       and strong_sell = ${stockRecommendation.strong_sell}
                       and recommended_timestamp = ${stockRecommendation.recommended_timestamp}
        """
      .update
      .run.transact(xa).unsafeRunSync
  }

  def insertStockRecommendation(stockRecommendation: StockRecommendation): Unit ={
    val queryStockRec = getStockRecommendation(stockRecommendation)
    val now = Util.NOW_TIMESTAMP
    if(queryStockRec.isEmpty){
      Util.printLog("StockRecommendationQuery insertStockRecommendation, symbol=%s, recommended_timestamp=%s".format(stockRecommendation.stock, stockRecommendation.recommended_timestamp))
      sql"""
INSERT INTO finance.stock_recommendation(
            stock, strong_buy, buy, hold, sell, strong_sell, recommended_timestamp,
            created_timestamp)
    VALUES (${stockRecommendation.stock}, ${stockRecommendation.strong_buy}, ${stockRecommendation.buy}, ${stockRecommendation.hold},  ${stockRecommendation.sell}, ${stockRecommendation.strong_sell},  ${stockRecommendation.recommended_timestamp},
            ${now});
          """
        .update
        .run.transact(xa).unsafeRunSync
      }
    }
}

