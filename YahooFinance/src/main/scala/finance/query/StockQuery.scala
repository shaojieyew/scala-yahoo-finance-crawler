package finance.query

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}

import doobie.implicits._
import finance.Util
import finance.model.{Stock, StockSeeder}

object StockQuery extends Database{

  def getStock(symbol: String): Option[Stock] ={
    Util.printLog("StockQuery getStock, symbol=%s".format(symbol))
    sql"""
        select symbol, name, industry, sector, country, market, exchange, website,
            description, quote_type, exchange_timezone_name, is_esg_populated, is_tradeable, 0
        from finance.stock
        where symbol = $symbol
        """
      .query[Stock]
      .option.transact(xa).unsafeRunSync
  }

  def getStockLastUpdate(symbol: String): Option[Timestamp] ={
    Util.printLog("StockQuery getStockLastUpdate, symbol=%s".format(symbol))
    sql"""
        select updated_timestamp
        from finance.stock
        where symbol = $symbol
        """
      .query[Option[Timestamp]]
      .option.transact(xa).unsafeRunSync.getOrElse(Option.empty)
  }

  def insertUpdateStock(stock: Stock): Unit ={
    insertStock(stock)
    // if(getStock(stock.symbol).isEmpty) insertStock(stock) else updateStock(stock)
  }

  def insertStock(stock: Stock): Unit ={
    Util.printLog("StockQuery insertStock, symbol=%s".format(stock.symbol))
    val now: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    sql"""
    INSERT INTO finance.stock(
            symbol, name, industry, sector, country, market, exchange, website,
            description, quote_type, exchange_timezone_name, is_esg_populated, is_tradeable, created_timestamp,
            updated_timestamp)
    VALUES (${stock.symbol}, ${stock.name}, ${stock.industry}, ${stock.sector}, ${stock.country},
      ${stock.market}, ${stock.exchange}, ${stock.website},${stock.description},
      ${stock.quote_type}, ${stock.exchange_timezone_name},
      ${stock.is_esg_populated}, ${stock.is_tradeable},
      ${now}, ${now})

      ON CONFLICT (symbol)
      DO
      UPDATE
      SET name=${stock.name}, industry=${stock.industry}, sector=${stock.sector},
      country=${stock.country}, market=${stock.market},
      exchange=${stock.exchange}, website=${stock.website}, description=${stock.description},
      quote_type=${stock.quote_type}, exchange_timezone_name=${stock.exchange_timezone_name},
      is_esg_populated=${stock.is_esg_populated}, is_tradeable=${stock.is_tradeable},
      updated_timestamp=$now
        """
      .update
      .run.transact(xa).unsafeRunSync

     insertCompanySize(stock,now)
  }


  def updateStock(stock: Stock): Unit ={
    Util.printLog("StockQuery updateStock, symbol=%s".format(stock.symbol))
    val now: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    sql"""
      UPDATE finance.stock
      SET name=${stock.name}, industry=${stock.industry}, sector=${stock.sector},
      country=${stock.country}, market=${stock.market},
      exchange=${stock.exchange}, website=${stock.website}, description=${stock.description},
      quote_type=${stock.quote_type}, exchange_timezone_name=${stock.exchange_timezone_name},
      is_esg_populated=${stock.is_esg_populated}, is_tradeable=${stock.is_tradeable},
      updated_timestamp=$now
      WHERE symbol=${stock.symbol};
        """
      .update
      .run.transact(xa).unsafeRunSync

      insertCompanySize(stock,now)
  }


  def getCompanySize(symbol: String): Option[Int] ={
    Util.printLog("StockQuery getCompanysize, symbol=%s".format(symbol))
    sql"""
        select count
        from finance.stock_company_size
        where stock = $symbol
        order by created_timestamp desc
        limit 1
        """
      .query[Option[Int]]
      .option.transact(xa).unsafeRunSync.getOrElse(Option.empty)
  }
  def insertCompanySize(stock: Stock, now: Timestamp): Unit ={
    val company_size=getCompanySize(stock.symbol)
    if(stock.full_time_employees.nonEmpty &&
    (company_size.isEmpty || (company_size.nonEmpty && company_size.get!=stock.full_time_employees.get))){
      Util.printLog("StockQuery insertCompanySize, symbol=%s, size=%s".format(stock.symbol,company_size.toString))
      sql"""
      INSERT INTO finance.stock_company_size(
        stock, count, created_timestamp)
      VALUES (${stock.symbol}, ${stock.full_time_employees}, ${now});
          """
        .update
        .run.transact(xa).unsafeRunSync
    }
  }
  def getAllStockSeeder(): List[StockSeeder] ={
    Util.printLog("StockQuery getAllStockSeeder")
    sql"""
        select symbol,src,created_timestamp,updated_timestamp from finance.stock_seeder
        """
      .query[StockSeeder]
      .nel
      .transact(xa)
      .unsafeRunSync.toList
  }
}

