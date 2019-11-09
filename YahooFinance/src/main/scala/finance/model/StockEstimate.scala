package finance.model

import java.sql.Timestamp

import finance.query.{StockEarningEstimateQuery, StockRevenueEstimateQuery}

trait StockAnalysisEstimate{
  def insertStockEstimateQuery()
}
case class StockRevenueEstimate(stock: String,est_date: Timestamp,
                                period: String, end_date: Timestamp,
                                est_avg: Long, est_low: Long, est_high: Long,
                                no_analyst: Int, growth: Float) extends StockAnalysisEstimate{
  def insertStockEstimateQuery(): Unit ={
    StockRevenueEstimateQuery.insertStockRevenueEstimateQuery(this)
  }
}
case class StockEarningEstimate(stock: String, est_date: Timestamp,
                                period: String, end_date: Timestamp,
                                est_avg: Float, est_low: Float, est_high: Float,
                                no_analyst: Int, growth: Float) extends StockAnalysisEstimate{
  def insertStockEstimateQuery(): Unit ={
    StockEarningEstimateQuery.insertStockEarningEstimateQuery(this)
  }
}