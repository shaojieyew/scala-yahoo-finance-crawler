package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._
import finance.Util
import finance.model.{Stock, StockPrice, StockSeeder}

object StockPriceQuery extends Database{

  def getStockPriceLastInsertDate(stock:String): Option[Timestamp] ={
    Util.printLog("getStockPriceLastInsertDate, symbol=%s".format(stock))
    val last_update= sql"""
        select max(date)
        from finance.stock_price
        where stock = $stock
        """
      .query[Option[Timestamp]]
      .option.transact(xa).unsafeRunSync.get
    Util.printLog("gotStockPriceLastInsertDate, symbol=%s, last_update=%s".format(stock,last_update))
    last_update
  }

  def getStockPrice(stock: String, date: Timestamp): Option[StockPrice] ={
    Util.printLog("getStockPrice, symbol=%s, date=%s".format(stock, date))
    sql"""
        select stock, open, high, low, close, adj_close, vol, date
        from finance.stock_price
        where stock = $stock and date = $date
        """
      .query[StockPrice]
      .option.transact(xa).unsafeRunSync
  }

  def insertStockPrice(stockPrice: StockPrice): Unit ={
    Util.printLog("insertStockPrice, symbol=%s, date=%s".format(stockPrice.stock, stockPrice.date))
    sql"""
    INSERT INTO finance.stock_price(
            stock, open, high, low, close, adj_close, vol, date)
    VALUES (${stockPrice.stock}, ${stockPrice.open}, ${stockPrice.high}, ${stockPrice.low}, ${stockPrice.close}, ${stockPrice.adj_close}, ${stockPrice.vol}, ${stockPrice.date});
        """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

