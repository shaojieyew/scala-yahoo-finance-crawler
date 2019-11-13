package finance.model

import java.sql.Timestamp

import finance.query.{StockBalanceQuery, StockCashFlowQuery, StockFinancialStatsQuery, StockIncomeQuery, StockShareStatsQuery, StockValuationStatsQuery}
import finance.scraper.yahoo.{StockFinanceScraper, StockStatsScraper}

abstract class StockStats{
  def insertStockStats()
}
case class StockValuationStats(stock: String,  date: Timestamp,
                                    marketCap: Option[Long] = Option.empty,
                                    enterpriseValue: Option[Long] = Option.empty,
                                    trailingPE: Option[Float] = Option.empty,
                                    forwardPE: Option[Float] = Option.empty,
                                    pegRatio_5years_expected: Option[Float] = Option.empty,
                                    priceToSales_ttm : Option[Float] = Option.empty,
                                    priceToBook_recentQtr: Option[Float] = Option.empty,
                                    enterpriseToRevenue : Option[Float] = Option.empty,
                                    enterpriseToEbitda: Option[Float] = Option.empty,
                                    beta  : Option[Float] = Option.empty
                                ) extends StockStats {
  override def insertStockStats(): Unit = StockValuationStatsQuery.insertStockValuationStats(this)
}

case class StockFinancialStats(stock: String,  date: Timestamp,
                                 profitMargins : Option[Float] = Option.empty,
                                 operatingMargins_ttm: Option[Float] = Option.empty,
                                 grossMargins : Option[Float] = Option.empty,
                                //Management Effectiveness TTM
                                 returnOnEquity_ttm : Option[Float] = Option.empty,
                                 returnOnAssets_ttm : Option[Float] = Option.empty,
                                //Income and Balance
                                 revenuePerShare_ttm : Option[Float] = Option.empty,
                                 revenueGrowth_yoy : Option[Float] = Option.empty,
                                 dilutedEps_ttm : Option[Float] = Option.empty,
                                 earningsQuarterlyGrowth_yoy : Option[Float] = Option.empty,
                                 earningsGrowth : Option[Float] = Option.empty,
                                 totalCashPerShare_recentQtr : Option[Float] = Option.empty,
                                 debtToEquity_recentQtr : Option[Float] = Option.empty,
                                 currentRatio_recentQtr : Option[Float] = Option.empty,
                                 bookValuePerShare_recentQtr : Option[Float] = Option.empty,
                                )extends StockStats {
  override def insertStockStats(): Unit = StockFinancialStatsQuery.insertStockFinancialStats(this)
}


case class StockShareStats(stock: String,  date: Timestamp,
                              sharesOutstanding  : Option[Long] = Option.empty,
                              floatShares  : Option[Long] = Option.empty,
                              heldPercentInstitutions  : Option[Float] = Option.empty,
                              heldPercentInsiders : Option[Float] = Option.empty,
                              sharesShortPriorMonth  : Option[Long] = Option.empty,
                              sharesShort : Option[Long] = Option.empty,
                              shortRatio  : Option[Float] = Option.empty,
                              shortPercentOfFloat : Option[Float] = Option.empty,
                              sharesPercentSharesOut  : Option[Float] = Option.empty
                          )extends StockStats {
  override def insertStockStats(): Unit = StockShareStatsQuery.insertStockShareStats(this)
}



object StockStats{
  def insertStockStats(symbol:String): Unit ={
    StockStatsScraper.get(symbol).foreach(stockStats=>{
      stockStats.insertStockStats()
    })
  }
}