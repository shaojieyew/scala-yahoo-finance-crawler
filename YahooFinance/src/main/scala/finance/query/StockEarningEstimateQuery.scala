package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import doobie.implicits._
import finance.Util
import finance.model._

object StockEarningEstimateQuery extends Database{

  def getStockEarningEstimate(stockEarningEstimate: StockEarningEstimate): Option[StockEarningEstimate] ={
    Util.printLog("StockEarningEstimateQuery getStockEarningEstimate, symbol=%s, est_date=%s, end_date=%s, period=%s".format(stockEarningEstimate.stock, stockEarningEstimate.est_date, stockEarningEstimate.end_date, stockEarningEstimate.period))
    sql"""
        SELECT stock, est_date, period, end_date, est_avg,  est_low, est_high,no_analyst, growth
        FROM finance.stock_earning_estimate
        where stock = ${stockEarningEstimate.stock}
                       and period = ${stockEarningEstimate.period}
                       and est_date = ${stockEarningEstimate.est_date}
                       and end_date = ${stockEarningEstimate.end_date}
        """
      .query[StockEarningEstimate]
      .option
      .transact(xa)
      .unsafeRunSync
  }

  def insertStockEarningEstimateQuery(stockEarningEstimate: StockEarningEstimate): Unit ={
    val queryStockRec = getStockEarningEstimate(stockEarningEstimate)
    val now = Util.NOW_TIMESTAMP
    if(queryStockRec.isEmpty){
      Util.printLog("StockEarningEstimateQuery insertStockEarningEstimateQuery, symbol=%s, est_date=%s, end_date=%s, period=%s".format(stockEarningEstimate.stock, stockEarningEstimate.est_date, stockEarningEstimate.end_date, stockEarningEstimate.period))
      sql"""
        INSERT INTO finance.stock_earning_estimate(
            stock, est_date, period, end_date, est_avg, est_high, est_low,
            growth, no_analyst, updated_timestamp)
    VALUES (${stockEarningEstimate.stock}, ${stockEarningEstimate.est_date}, ${stockEarningEstimate.period}, ${stockEarningEstimate.end_date},
     ${stockEarningEstimate.est_avg}, ${stockEarningEstimate.est_high}, ${stockEarningEstimate.est_low},
            ${stockEarningEstimate.growth}, ${stockEarningEstimate.no_analyst}, ${now});
          """
        .update
        .run.transact(xa).unsafeRunSync
      }else{
      updateStockEarningEstimateQuery(stockEarningEstimate)
    }
    }


  def updateStockEarningEstimateQuery(stockEarningEstimate: StockEarningEstimate): Unit ={
    val estDate = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS))
    val now = Util.NOW_TIMESTAMP
    if(stockEarningEstimate.est_date.equals(estDate)) {
      Util.printLog("StockEarningEstimateQuery updateStockEarningEstimateQuery, symbol=%s, est_date=%s, end_date=%s, period=%s est_avg=%s, est_high=%s, est_low=%s, no_analyst=%s growth=%s".format(stockEarningEstimate.stock, stockEarningEstimate.est_date, stockEarningEstimate.end_date, stockEarningEstimate.period, stockEarningEstimate.est_avg, stockEarningEstimate.est_high, stockEarningEstimate.est_low, stockEarningEstimate.no_analyst, stockEarningEstimate.growth))
      sql"""
        UPDATE finance.stock_earning_estimate
   SET est_avg=${stockEarningEstimate.est_avg}, est_high=${stockEarningEstimate.est_high},
       est_low=${stockEarningEstimate.est_low}, growth=${stockEarningEstimate.growth}, no_analyst=${stockEarningEstimate.no_analyst},
       updated_timestamp=${now}
        where stock = ${stockEarningEstimate.stock}
                       and period = ${stockEarningEstimate.period}
                       and est_date = ${stockEarningEstimate.est_date}
                       and end_date = ${stockEarningEstimate.end_date};
          """
        .update
        .run.transact(xa).unsafeRunSync
    } else{
      Util.printLog("StockEarningEstimateQuery updateStockEarningEstimateQuery, symbol=%s, est_date=%s, end_date=%s, period=%s est_avg=%s".format(stockEarningEstimate.stock, stockEarningEstimate.est_date, stockEarningEstimate.end_date, stockEarningEstimate.period, stockEarningEstimate.est_avg))
      sql"""
        UPDATE finance.stock_earning_estimate
   SET est_avg=${stockEarningEstimate.est_avg}, updated_timestamp=${now}
        where stock = ${stockEarningEstimate.stock}
                       and period = ${stockEarningEstimate.period}
                       and est_date = ${stockEarningEstimate.est_date}
                       and end_date = ${stockEarningEstimate.end_date};
          """
        .update
        .run.transact(xa).unsafeRunSync
    }
  }
}

