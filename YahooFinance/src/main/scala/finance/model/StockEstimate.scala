package finance.model

import java.sql.Timestamp

import finance.query.{StockEarningEstimateQuery, StockRevenueEstimateQuery}

trait StockAnalysisEstimate{
  def insertStockEstimateQuery()
}
case class StockRevenueEstimate(stock: String,est_date: Timestamp,
                                period: String, end_date: Timestamp,
                                est_avg:  Option[Long], est_low: Option[Long], est_high: Option[Long],
                                no_analyst:  Option[Int], growth: Option[Float]) extends StockAnalysisEstimate{
  def insertStockEstimateQuery(): Unit ={
    StockRevenueEstimateQuery.insertStockRevenueEstimateQuery(this)
  }
}
case class StockEarningEstimate(stock: String, est_date: Timestamp,
                                period: String, end_date: Timestamp,
                                est_avg:  Option[Float], est_low: Option[Float], est_high: Option[Float],
                                no_analyst:  Option[Int], growth: Option[Float]) extends StockAnalysisEstimate{
  def insertStockEstimateQuery(): Unit ={
    StockEarningEstimateQuery.insertStockEarningEstimateQuery(this)
  }
}