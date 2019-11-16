package finance.scraper.yahoo

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneOffset}
import java.util

import com.jayway.jsonpath.JsonPath
import finance.Util
import finance.model.{StockFinance, StockIncome}
import io.circe.{Json, parser}
import net.minidev.json.{JSONArray, JSONObject}

import scala.collection.mutable
import scala.scalajs.js.JSON

object testStockTrend extends App{
  //println(StockFinanceScraper.getStockTrends())
  StockFinanceScraper.get("GOOGL")
}

object StockFinanceScraper {

  def get(symbol: String, start: LocalDateTime = LocalDateTime.now().minusYears(10), end: LocalDateTime = LocalDateTime.now()): List[StockFinance] = {
    var final_result: Map[(Timestamp, String, String), StockFinance] = Map.empty

    val period1 = start.toEpochSecond(ZoneOffset.UTC)
    val period2 = end.toEpochSecond(ZoneOffset.UTC)

    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val params = (for (t <- StockFinance.TYPES; col <- StockFinance.INCOME_COLS ::: StockFinance.BALANCE_COLS ::: StockFinance.CASHFLOW_COLS) yield "%s%s".format(t, col)).mkString(",")
    var url = "https://query1.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/%s" +
      "?lang=en-SG&region=SG&symbol=%s&padTimeSeries=true&type=%s" +
      "&merge=false&period1=%s&period2=%s&corsDomain=sg.finance.yahoo.com"
    url = url.format(symbol, symbol, params, period1, period2)
    var max_try = 3
    var tried = 0
    do{
      try{
        val r = requests.get(url)
        tried = tried +1
        Util.printLog("StockFinanceScraper getStockTrends url=%s".format(r.url))
        val json = Option(parser.parse(r.text).getOrElse(Json.Null))
      //println(json)
        if (json.nonEmpty) {
        //val results = JsonPath.read[JSONArray](json.get.toString(),"$.timeseries.result[?(@.timestamp)]")

          val cols = for (t <- StockFinance.TYPES; col <- StockFinance.INCOME_COLS ::: StockFinance.BALANCE_COLS ::: StockFinance.CASHFLOW_COLS) yield "%s%s".format(t, col)
          for (col <- cols) {
            val results = JsonPath.read[JSONArray](json.get.toString(), "$.timeseries.result[?(@.%s)].%s[?(@.reportedValue)]".format(col, col)).toJSONString()
            parser.parse(results).foreach(x => {
              val normalised = col.toString.replaceFirst("trailing", "").replaceFirst("annual", "").replaceFirst("quarterly", "")
              x.asArray.foreach(recs => {
                recs.foreach(rec => {
                  val asOfDate = JsonPath.read(rec.toString(), "$.asOfDate").toString
                  val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond(sdf.parse(asOfDate).getTime / 1000, 0, ZoneOffset.of("+08:00")))
                  val period = JsonPath.read(rec.toString(), "$.periodType").toString
                  val currency = JsonPath.read(rec.toString(), "$.currencyCode").toString
                  val value = JsonPath.read(rec.toString(), "$.reportedValue.raw").toString.toFloat
                  val finance_type = normalised
                  /*
                  println(finance_type)
                  println(date)
                  println(period)
                  println(currency)
                  println(value)
                  */
                  val stockFinance = StockFinance.getInstance(symbol, date, period, value, finance_type, currency)
                  val key = (date, period, stockFinance.getClass.getName.split("\\.").last)
                  if (final_result.get(key).isEmpty) {
                    final_result += (key) -> StockFinance.getInstance(symbol, date, period, value, finance_type, currency)
                  } else {
                    val obj = final_result.get(key).get
                    obj.updateInstance(value, finance_type)
                    final_result += (key) -> obj
                  }
                })
              })
            })
          }
        }
        tried = max_try
      }
      catch{
        case _: Exception => Util.printLog("Response failed count: "+tried)
      }
    }while(tried < max_try)
    /*
    final_result.foreach(k=>{
      Util.printLog("======="+k._1+"======")
      k._2.debugVars()
    })

    */

    final_result.values.toList
  }


/*
  def getStockTrends(): Option[Json] = {
    val symbol="GOOGL"
    val period1=493590046
    val period2=1572110359
    //income
    val income_trailing = "trailingEbitda,trailingWeighteAverageShare,trailingDilutedAverageShares,trailingBasicAverageShares,trailingearningsPerShare,trailingDilutedEPS,trailingBasicEPS,trailingNetIncomeCommonStockholders,trailingNetIncome,trailingNetIncomeContinuousOperations,trailingTaxProvision,trailingPretaxIncome,trailingOtherIncomeExpense,trailingInterestExpense,trailingOperatingIncome,trailingOperatingExpense,trailingSellingGeneralAndAdministration,trailingResearchAndDevelopment,trailingGrossProfit,trailingCostOfRevenue,trailingTotalRevenue"
    val income_annual = "annualEbitda,annualWeighteAverageShare,annualDilutedAverageShares,annualBasicAverageShares,annualearningsPerShare,annualDilutedEPS,annualBasicEPS,annualNetIncomeCommonStockholders,annualNetIncome,annualNetIncomeContinuousOperations,annualTaxProvision,annualPretaxIncome,annualOtherIncomeExpense,annualInterestExpense,annualOperatingIncome,annualOperatingExpense,annualSellingGeneralAndAdministration,annualResearchAndDevelopment,annualGrossProfit,annualCostOfRevenue,annualTotalRevenue"
    val income_quarterly ="quarterlyEbitda,quarterlyWeighteAverageShare,quarterlyDilutedAverageShares,quarterlyBasicAverageShares,quarterlyearningsPerShare,quarterlyDilutedEPS,quarterlyBasicEPS,quarterlyNetIncomeCommonStockholders,quarterlyNetIncome,quarterlyNetIncomeContinuousOperations,quarterlyTaxProvision,quarterlyPretaxIncome,quarterlyOtherIncomeExpense,quarterlyInterestExpense,quarterlyOperatingIncome,quarterlyOperatingExpense,quarterlySellingGeneralAndAdministration,quarterlyResearchAndDevelopment,quarterlyGrossProfit,quarterlyCostOfRevenue,quarterlyTotalRevenue"

    //balance
    val balance_trailing="trailingTotalAssets,trailingStockholdersEquity,trailingGainsLossesNotAffectingRetainedEarnings,trailingRetainedEarnings,trailingCapitalStock,trailingTotalLiabilitiesNetMinorityInterest,trailingTotalNonCurrentLiabilitiesNetMinorityInterest,trailingOtherNonCurrentLiabilities,trailingNonCurrentDeferredRevenue,trailingNonCurrentDeferredTaxesLiabilities,trailingLongTermDebt,trailingCurrentLiabilities,trailingOtherCurrentLiabilities,trailingCurrentDeferredRevenue,trailingCurrentAccruedExpenses,trailingIncomeTaxPayable,trailingAccountsPayable,trailingCurrentDebt,trailingTotalNonCurrentAssets,trailingOtherNonCurrentAssets,trailingOtherIntangibleAssets,trailingGoodwill,trailingInvestmentsAndAdvances,trailingNetPPE,trailingAccumulatedDepreciation,trailingGrossPPE,trailingCurrentAssets,trailingOtherCurrentAssets,trailingInventory,trailingAccountsReceivable,trailingCashCashEquivalentsAndMarketableSecurities,trailingOtherShortTermInvestments,trailingCashAndCashEquivalents"
    val balance_annual="annualTotalAssets,annualStockholdersEquity,annualGainsLossesNotAffectingRetainedEarnings,annualRetainedEarnings,annualCapitalStock,annualTotalLiabilitiesNetMinorityInterest,annualTotalNonCurrentLiabilitiesNetMinorityInterest,annualOtherNonCurrentLiabilities,annualNonCurrentDeferredRevenue,annualNonCurrentDeferredTaxesLiabilities,annualLongTermDebt,annualCurrentLiabilities,annualOtherCurrentLiabilities,annualCurrentDeferredRevenue,annualCurrentAccruedExpenses,annualIncomeTaxPayable,annualAccountsPayable,annualCurrentDebt,annualTotalNonCurrentAssets,annualOtherNonCurrentAssets,annualOtherIntangibleAssets,annualGoodwill,annualInvestmentsAndAdvances,annualNetPPE,annualAccumulatedDepreciation,annualGrossPPE,annualCurrentAssets,annualOtherCurrentAssets,annualInventory,annualAccountsReceivable,annualCashCashEquivalentsAndMarketableSecurities,annualOtherShortTermInvestments,annualCashAndCashEquivalents"
    val balance_quarterly="quarterlyTotalAssets,quarterlyStockholdersEquity,quarterlyGainsLossesNotAffectingRetainedEarnings,quarterlyRetainedEarnings,quarterlyCapitalStock,quarterlyTotalLiabilitiesNetMinorityInterest,quarterlyTotalNonCurrentLiabilitiesNetMinorityInterest,quarterlyOtherNonCurrentLiabilities,quarterlyNonCurrentDeferredRevenue,quarterlyNonCurrentDeferredTaxesLiabilities,quarterlyLongTermDebt,quarterlyCurrentLiabilities,quarterlyOtherCurrentLiabilities,quarterlyCurrentDeferredRevenue,quarterlyCurrentAccruedExpenses,quarterlyIncomeTaxPayable,quarterlyAccountsPayable,quarterlyCurrentDebt,quarterlyTotalNonCurrentAssets,quarterlyOtherNonCurrentAssets,quarterlyOtherIntangibleAssets,quarterlyGoodwill,quarterlyInvestmentsAndAdvances,quarterlyNetPPE,quarterlyAccumulatedDepreciation,quarterlyGrossPPE,quarterlyCurrentAssets,quarterlyOtherCurrentAssets,quarterlyInventory,quarterlyAccountsReceivable,quarterlyCashCashEquivalentsAndMarketableSecurities,quarterlyOtherShortTermInvestments,quarterlyCashAndCashEquivalents"

    //cashflow
    val cashflow_trailing ="trailingFreeCashFlow,trailingCapitalExpenditure,trailingOperatingCashFlow,trailingEndCashPosition,trailingBeginningCashPosition,trailingChangeInCashSupplementalAsReported,trailingCashFlowFromContinuingFinancingActivities,trailingNetOtherFinancingCharges,trailingCashDividendsPaid,trailingRepurchaseOfCapitalStock,trailingCommonStockIssuance,trailingRepaymentOfDebt,trailingInvestingCashFlow,trailingNetOtherInvestingChanges,trailingSaleOfInvestment,trailingPurchaseOfInvestment,trailingPurchaseOfBusiness,trailingOtherNonCashItems,trailingChangeInAccountPayable,trailingChangeInInventory,trailingChangesInAccountReceivables,trailingChangeInWorkingCapital,trailingStockBasedCompensation,trailingDeferredIncomeTax,trailingDepreciationAndAmortization,trailingNetIncome"
    val cashflow_annual ="annualFreeCashFlow,annualCapitalExpenditure,annualOperatingCashFlow,annualEndCashPosition,annualBeginningCashPosition,annualChangeInCashSupplementalAsReported,annualCashFlowFromContinuingFinancingActivities,annualNetOtherFinancingCharges,annualCashDividendsPaid,annualRepurchaseOfCapitalStock,annualCommonStockIssuance,annualRepaymentOfDebt,annualInvestingCashFlow,annualNetOtherInvestingChanges,annualSaleOfInvestment,annualPurchaseOfInvestment,annualPurchaseOfBusiness,annualOtherNonCashItems,annualChangeInAccountPayable,annualChangeInInventory,annualChangesInAccountReceivables,annualChangeInWorkingCapital,annualStockBasedCompensation,annualDeferredIncomeTax,annualDepreciationAndAmortization,annualNetIncome"
    val cashflow_quarterly = "quarterlyFreeCashFlow,quarterlyCapitalExpenditure,quarterlyOperatingCashFlow,quarterlyEndCashPosition,quarterlyBeginningCashPosition,quarterlyChangeInCashSupplementalAsReported,quarterlyCashFlowFromContinuingFinancingActivities,quarterlyNetOtherFinancingCharges,quarterlyCashDividendsPaid,quarterlyRepurchaseOfCapitalStock,quarterlyCommonStockIssuance,quarterlyRepaymentOfDebt,quarterlyInvestingCashFlow,quarterlyNetOtherInvestingChanges,quarterlySaleOfInvestment,quarterlyPurchaseOfInvestment,quarterlyPurchaseOfBusiness,quarterlyOtherNonCashItems,quarterlyChangeInAccountPayable,quarterlyChangeInInventory,quarterlyChangesInAccountReceivables,quarterlyChangeInWorkingCapital,quarterlyStockBasedCompensation,quarterlyDeferredIncomeTax,quarterlyDepreciationAndAmortization,quarterlyNetIncome"
    var url="https://query1.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/%s"+
      "?lang=en-SG&region=SG&symbol=%s&padTimeSeries=true&type=%s"+
      "&merge=false&period1=%d&period2=%d&corsDomain=sg.finance.yahoo.com"


    val cols=income_trailing+","+income_annual+","+income_quarterly+","+balance_trailing+","+balance_annual+","+balance_quarterly+","+cashflow_trailing+","+cashflow_annual+","+cashflow_quarterly
    url = String.format(url,symbol,symbol,cols,period1,period2)

    val r=requests.get(url)
    Util.printLog("getStockTrends url=%s".format(r.url))
    Option(parser.parse(r.text).getOrElse(Json.Null))
  }
  */
}
