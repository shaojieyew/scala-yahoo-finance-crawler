package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._
import finance.Util
import finance.model.{StockFinancialStats, StockValuationStats}

object StockFinancialStatsQuery extends Database{

  def insertStockFinancialStats(stockFinancialStats: StockFinancialStats): Unit ={
    val now = Timestamp.valueOf(LocalDateTime.now)
    Util.printLog("StockFinancialStatsQuery insertStockFinancialStats, symbol=%s, date=%s".format(stockFinancialStats.stock, stockFinancialStats.date))
    sql"""
INSERT INTO finance.stock_financial_stats(
            stock, date, profitmargins, operatingmargins_ttm, grossmargins,
            returnonequity_ttm, returnonassets_ttm, revenuepershare_ttm,
            revenuegrowth_yoy, dilutedeps_ttm, earningsquarterlygrowth_yoy,
            earningsgrowth, totalcashpershare_recentqtr, debttoequity_recentqtr,
            currentratio_recentqtr, bookvaluepershare_recentqtr, updated_timestamp)
    VALUES (${stockFinancialStats.stock}, ${stockFinancialStats.date}, ${stockFinancialStats.profitMargins}, ${stockFinancialStats.operatingMargins_ttm}, ${stockFinancialStats.grossMargins},
            ${stockFinancialStats.returnOnEquity_ttm}, ${stockFinancialStats.returnOnAssets_ttm}, ${stockFinancialStats.revenuePerShare_ttm},
            ${stockFinancialStats.revenueGrowth_yoy}, ${stockFinancialStats.dilutedEps_ttm}, ${stockFinancialStats.earningsQuarterlyGrowth_yoy},
            ${stockFinancialStats.earningsGrowth}, ${stockFinancialStats.totalCashPerShare_recentQtr}, ${stockFinancialStats.debtToEquity_recentQtr},
            ${stockFinancialStats.currentRatio_recentQtr}, ${stockFinancialStats.bookValuePerShare_recentQtr}, $now)
    ON CONFLICT (stock, date)
    DO
      UPDATE
   SET stock=${stockFinancialStats.stock}, date=${stockFinancialStats.date}, profitmargins=${stockFinancialStats.profitMargins}, operatingmargins_ttm=${stockFinancialStats.operatingMargins_ttm}, grossmargins=${stockFinancialStats.grossMargins},
       returnonequity_ttm=${stockFinancialStats.returnOnEquity_ttm}, returnonassets_ttm=${stockFinancialStats.returnOnAssets_ttm}, revenuepershare_ttm=${stockFinancialStats.revenuePerShare_ttm},
       revenuegrowth_yoy=${stockFinancialStats.revenueGrowth_yoy}, dilutedeps_ttm=${stockFinancialStats.dilutedEps_ttm}, earningsquarterlygrowth_yoy=${stockFinancialStats.earningsQuarterlyGrowth_yoy},
       earningsgrowth=${stockFinancialStats.earningsGrowth}, totalcashpershare_recentqtr=${stockFinancialStats.totalCashPerShare_recentQtr}, debttoequity_recentqtr=${stockFinancialStats.debtToEquity_recentQtr},
       currentratio_recentqtr=${stockFinancialStats.currentRatio_recentQtr}, bookvaluepershare_recentqtr=${stockFinancialStats.bookValuePerShare_recentQtr}, updated_timestamp=$now
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

