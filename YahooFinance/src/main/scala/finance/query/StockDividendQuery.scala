package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._
import finance.Util
import finance.model.{Stock, StockDividend, StockSeeder}

object StockDividendQuery extends Database{

  def getStockDividendLastInsertDate(stock:String): Option[Timestamp] ={
    Util.printLog("getStockDividendLastInsertDate, symbol=%s".format(stock))
    val last_update= sql"""
        select max(date)
        from finance.stock_Dividend
        where stock = $stock
        """
      .query[Option[Timestamp]]
      .option.transact(xa).unsafeRunSync.get
    Util.printLog("gotStockDividendLastInsertDate, symbol=%s, last_update=%s".format(stock,last_update))
    last_update
  }

  def getStockDividend(stock: String, date: Timestamp): Option[StockDividend] ={
    Util.printLog("getStockDividend, symbol=%s, date=%s".format(stock, date))
    sql"""
        select stock, dividend, date
        from finance.stock_dividend
        where stock = $stock and date = $date
        """
      .query[StockDividend]
      .option.transact(xa).unsafeRunSync
  }

  def insertStockDividend(stockDividend: StockDividend): Unit ={
    Util.printLog("insertStockDividend, symbol=%s, date=%s".format(stockDividend.stock, stockDividend.date))
    sql"""
    INSERT INTO finance.stock_dividend(
            stock, dividend, date)
    VALUES (${stockDividend.stock}, ${stockDividend.dividend}, ${stockDividend.date})
    ON CONFLICT (stock, date)
    DO
      UPDATE
      SET dividend = ${stockDividend.dividend};
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

