package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._
import finance.Util
import finance.model.{StockCashFlow, StockValuationStats}

object StockValuationStatsQuery extends Database{

  def insertStockValuationStats(stockValuationStats: StockValuationStats): Unit ={
    val now = Timestamp.valueOf(LocalDateTime.now)
    Util.printLog("StockValuationStatsQuery insertStockValuationStats, symbol=%s, date=%s".format(stockValuationStats.stock, stockValuationStats.date))
    sql"""
INSERT INTO finance.stock_valuation_stats(
            stock, date, marketcap, enterprisevalue, trailingpe, forwardpe,
            pegratio_5years_expected, pricetosales_ttm, pricetobook_recentqtr,
            enterprisetorevenue, enterprisetoebitda, beta, updated_timestamp)
    VALUES (${stockValuationStats.stock}, ${stockValuationStats.date}, ${stockValuationStats.marketCap}, ${stockValuationStats.enterpriseValue}, ${stockValuationStats.trailingPE}, ${stockValuationStats.forwardPE},
            ${stockValuationStats.pegRatio_5years_expected}, ${stockValuationStats.priceToSales_ttm}, ${stockValuationStats.priceToBook_recentQtr},
            ${stockValuationStats.enterpriseToRevenue}, ${stockValuationStats.enterpriseToEbitda}, ${stockValuationStats.beta}, $now)
    ON CONFLICT (stock, date)
    DO
      UPDATE
   SET stock=${stockValuationStats.stock}, date=${stockValuationStats.date}, marketcap=${stockValuationStats.marketCap}, enterprisevalue=${stockValuationStats.enterpriseValue}, trailingpe=${stockValuationStats.trailingPE},
       forwardpe=${stockValuationStats.forwardPE}, pegratio_5years_expected=${stockValuationStats.pegRatio_5years_expected}, pricetosales_ttm=${stockValuationStats.priceToSales_ttm},
       pricetobook_recentqtr=${stockValuationStats.priceToBook_recentQtr}, enterprisetorevenue=${stockValuationStats.enterpriseToRevenue}, enterprisetoebitda=${stockValuationStats.enterpriseToEbitda},
       beta=${stockValuationStats.beta}, updated_timestamp=$now
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

