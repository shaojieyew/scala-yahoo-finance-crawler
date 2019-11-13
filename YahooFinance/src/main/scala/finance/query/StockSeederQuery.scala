package finance.query

import java.sql.Timestamp

import finance.model.StockSeeder
import doobie.implicits._
import java.time.LocalDateTime

import finance.Util

object StockSeederQuery extends Database{

  def main (args: Array[String]): Unit ={
    println(getStockSeeder("asdaa","aa").getOrElse(""))
  }

  def getAllStockSeeder(): List[StockSeeder] ={
    Util.printLog("StockSeederQuery getAllStockSeeder")
    sql"""
        select symbol,src,created_timestamp,updated_timestamp from finance.stock_seeder
        """
      .query[StockSeeder]
      .nel
      .transact(xa)
      .unsafeRunSync.toList
  }

  def getStockSeeder(symbol: String, src: String): Option[StockSeeder] ={
    Util.printLog("StockSeederQuery getStockSeeder, symbol=%s, src=%s".format(symbol, src))
    sql"""
        select symbol,src,created_timestamp,updated_timestamp from finance.stock_seeder
        where symbol = $symbol and src = $src
        """
      .query[StockSeeder]
      .option.transact(xa).unsafeRunSync
  }

  def insertStockSeeder(symbol: String, src: String): Unit ={
    Util.printLog("StockSeederQuery insertStockSeeder, symbol=%s, src=%s".format(symbol, src))
    val now: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    sql"""
       insert into finance.stock_seeder (symbol,src,created_timestamp,updated_timestamp)
       values ($symbol, $src, $now, $now)
        """
      .update
      .run.transact(xa).unsafeRunSync
  }


  def updateStockSeeder(symbol: String, src: String): Unit ={
    Util.printLog("StockSeederQuery updateStockSeeder, symbol=%s, src=%s".format(symbol, src))
    val now: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    sql"""
       update finance.stock_seeder set updated_timestamp = $now
       where symbol = $symbol and src = $src
        """
      .update
      .run.transact(xa).unsafeRunSync
  }

  def deleteStockSeeder(symbol: String, src: String): Unit ={
    Util.printLog("StockSeederQuery deleteStockSeeder, symbol=%s, src=%s".format(symbol, src))
    val now: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    sql"""
       delete from finance.stock_seeder
       where symbol = $symbol and src = $src
        """
      .update
      .run.transact(xa).unsafeRunSync
  }

  def deleteAllStockSeeder(): Unit ={
    Util.printLog("StockSeederQuery deleteAllStockSeeder")
    val now: Timestamp = Timestamp.valueOf(LocalDateTime.now)
    sql"""
       delete from finance.stock_seeder
        """
      .update
      .run.transact(xa).unsafeRunSync
  }
}

