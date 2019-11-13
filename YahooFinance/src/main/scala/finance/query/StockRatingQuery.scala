package finance.query

import doobie.implicits._
import finance.Util
import finance.model._

object StockRatingQuery extends Database{


  def getStockRatings(symbol: String): List[StockRating] ={
    Util.printLog("StockRatingQuery getStockRatings, symbol=%s".format(symbol))
    sql"""
        select stock,
                       firm,
                       from_grade,
                       to_grade,
                       action,
                       graded_timestamp from finance.stock_rating
                       where stock = $symbol
        """
      .query[StockRating]
      .nel
      .transact(xa)
      .unsafeRunSync.toList
  }

  def getStockRating(stockRating: StockRating): Option[StockRating] ={
    Util.printLog("StockRatingQuery getStockRating, symbol=%s, firm=%s, graded_timestamp=%s".format(stockRating.stock, stockRating.firm, stockRating.graded_timestamp))
    sql"""
        select stock,
                       firm,
                       from_grade,
                       to_grade,
                       action,
                       graded_timestamp from finance.stock_rating
                       where stock = ${stockRating.stock} and firm = ${stockRating.firm} and graded_timestamp = ${stockRating.graded_timestamp}
        """
      .query[StockRating]
      .option
      .transact(xa)
      .unsafeRunSync
  }

  def insertStockRating(stockRating: StockRating): Unit ={
    val now = Util.NOW_TIMESTAMP
    val queryStockRating=getStockRating(stockRating)
    if(queryStockRating.isEmpty){
      Util.printLog("StockRatingQuery insertStockRating, symbol=%s, firm=%s, graded_timestamp=%s".format(stockRating.stock, stockRating.firm, stockRating.graded_timestamp))
      sql"""
INSERT INTO finance.stock_rating(
            stock, firm, from_grade, to_grade, action, graded_timestamp,
            created_timestamp)
    VALUES (${stockRating.stock}, ${stockRating.firm}, ${stockRating.from_grade},
    ${stockRating.to_grade}, ${stockRating.action}, ${stockRating.graded_timestamp}, ${now});
          """
        .update
        .run.transact(xa).unsafeRunSync
    }
  }

}

