package finance.query

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._
import finance.Util
import finance.model.{StockFinancialStats, StockShareStats}

object StockShareStatsQuery extends Database{

  def insertStockShareStats(stockShareStats: StockShareStats): Unit ={
    val now = Timestamp.valueOf(LocalDateTime.now)
    Util.printLog("StockShareStatsQuery insertStockShareStats, symbol=%s, date=%s".format(stockShareStats.stock, stockShareStats.date))
    sql"""
INSERT INTO finance.stock_share_stats(
            stock, date, sharesoutstanding, floatshares, heldpercentinstitutions,
            heldpercentinsiders, sharesshortpriormonth, sharesshort, shortratio,
            shortpercentoffloat, sharespercentsharesout, updated_timestamp)
    VALUES (${stockShareStats.stock}, ${stockShareStats.date}, ${stockShareStats.sharesOutstanding}, ${stockShareStats.floatShares}, ${stockShareStats.heldPercentInstitutions},
            ${stockShareStats.heldPercentInsiders}, ${stockShareStats.sharesShortPriorMonth}, ${stockShareStats.sharesShort}, ${stockShareStats.shortRatio},
            ${stockShareStats.shortPercentOfFloat}, ${stockShareStats.sharesPercentSharesOut}, $now)
    ON CONFLICT (stock, date)
    DO
      UPDATE
   SET stock=${stockShareStats.stock}, date=${stockShareStats.date}, sharesoutstanding=${stockShareStats.sharesOutstanding}, floatshares=${stockShareStats.floatShares}, heldpercentinstitutions=${stockShareStats.heldPercentInstitutions},
       heldpercentinsiders=${stockShareStats.heldPercentInsiders}, sharesshortpriormonth=${stockShareStats.sharesShortPriorMonth}, sharesshort=${stockShareStats.sharesShort},
       shortratio=${stockShareStats.shortRatio}, shortpercentoffloat=${stockShareStats.shortPercentOfFloat}, sharespercentsharesout=${stockShareStats.sharesPercentSharesOut},
       updated_timestamp=$now
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

