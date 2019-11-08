package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import doobie.implicits._
import finance.Util
import finance.model._

object StockRevenueEstimateQuery extends Database{

  def getStockRevenueEstimate(stockRevenueEstimate: StockRevenueEstimate): Option[StockRevenueEstimate] ={
    Util.printLog("getStockRevenueEstimate, symbol=%s, est_date=%s, end_date=%s, period=%s".format(stockRevenueEstimate.stock, stockRevenueEstimate.est_date, stockRevenueEstimate.end_date, stockRevenueEstimate.period))
    sql"""
        SELECT stock, est_date, period, end_date, est_avg,  est_low, est_high,no_analyst, growth
        FROM finance.stock_revenue_estimate
        where stock = ${stockRevenueEstimate.stock}
                       and period = ${stockRevenueEstimate.period}
                       and est_date = ${stockRevenueEstimate.est_date}
                       and end_date = ${stockRevenueEstimate.end_date}
        """
      .query[StockRevenueEstimate]
      .option
      .transact(xa)
      .unsafeRunSync
  }

  def insertStockRevenueEstimateQuery(stockRevenueEstimate: StockRevenueEstimate): Unit ={
    val queryStockRec = getStockRevenueEstimate(stockRevenueEstimate)
    val now = Util.NOW_TIMESTAMP
    if(queryStockRec.isEmpty){
      Util.printLog("insertStockRevenueEstimateQuery, symbol=%s, est_date=%s, end_date=%s, period=%s".format(stockRevenueEstimate.stock, stockRevenueEstimate.est_date, stockRevenueEstimate.end_date, stockRevenueEstimate.period))
      sql"""
        INSERT INTO finance.stock_revenue_estimate(
            stock, est_date, period, end_date, est_avg, est_high, est_low,
            growth, no_analyst, updated_timestamp)
    VALUES (${stockRevenueEstimate.stock}, ${stockRevenueEstimate.est_date}, ${stockRevenueEstimate.period}, ${stockRevenueEstimate.end_date},
     ${stockRevenueEstimate.est_avg}, ${stockRevenueEstimate.est_high}, ${stockRevenueEstimate.est_low},
            ${stockRevenueEstimate.growth}, ${stockRevenueEstimate.no_analyst}, ${now});
          """
        .update
        .run.transact(xa).unsafeRunSync
    }else{
      updateStockRevenueEstimateQuery(stockRevenueEstimate)
    }
  }


  def updateStockRevenueEstimateQuery(stockRevenueEstimate: StockRevenueEstimate): Unit ={
    val estDate = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS))
    val now = Util.NOW_TIMESTAMP
    if(stockRevenueEstimate.est_date.equals(estDate)) {
      Util.printLog("updateStockRevenueEstimateQuery, symbol=%s, est_date=%s, end_date=%s, period=%s est_avg=%s, est_high=%s, est_low=%s, no_analyst=%s growth=%s".format(stockRevenueEstimate.stock, stockRevenueEstimate.est_date, stockRevenueEstimate.end_date, stockRevenueEstimate.period, stockRevenueEstimate.est_avg, stockRevenueEstimate.est_high, stockRevenueEstimate.est_low, stockRevenueEstimate.no_analyst, stockRevenueEstimate.growth))
      sql"""
        UPDATE finance.stock_revenue_estimate
   SET est_avg=${stockRevenueEstimate.est_avg}, est_high=${stockRevenueEstimate.est_high},
       est_low=${stockRevenueEstimate.est_low}, growth=${stockRevenueEstimate.growth}, no_analyst=${stockRevenueEstimate.no_analyst},
       updated_timestamp=${now}
        where stock = ${stockRevenueEstimate.stock}
                       and period = ${stockRevenueEstimate.period}
                       and est_date = ${stockRevenueEstimate.est_date}
                       and end_date = ${stockRevenueEstimate.end_date};
          """
        .update
        .run.transact(xa).unsafeRunSync
    } else{
      Util.printLog("updateStockRevenueEstimateQuery, symbol=%s, est_date=%s, end_date=%s, period=%s est_avg=%s".format(stockRevenueEstimate.stock, stockRevenueEstimate.est_date, stockRevenueEstimate.end_date, stockRevenueEstimate.period, stockRevenueEstimate.est_avg))
      sql"""
        UPDATE finance.stock_revenue_estimate
   SET est_avg=${stockRevenueEstimate.est_avg}, updated_timestamp=${now}
        where stock = ${stockRevenueEstimate.stock}
                       and period = ${stockRevenueEstimate.period}
                       and est_date = ${stockRevenueEstimate.est_date}
                       and end_date = ${stockRevenueEstimate.end_date};
          """
        .update
        .run.transact(xa).unsafeRunSync
    }
  }
}

