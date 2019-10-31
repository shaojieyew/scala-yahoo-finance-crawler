package YahooFinance

import io.circe.{Json, parser}
import scala.util.matching.Regex

object App {

  def main(args: Array[String]): Unit = {

    println(getListOfStocks())
    println(getStockDetails())
    println(getStockDetails("/analysis"))
    println(getStockTrends())

    def getStockTrends(): Option[Json] = {
      val symbol="GOOGL"
      val period1=493590046
      val period2=1572110359
      //income
      val income_trailing = "trailingEbitda,trailingWeighteAverageShare,trailingDilutedAverageShares,trailingBasicAverageShares,trailingearningsPerShare,trailingDilutedEPS,trailingBasicEPS,trailingNetIncomeCommonStockholders,trailingNetIncome,trailingNetIncomeContinuousOperations,trailingTaxProvision,trailingPretaxIncome,trailingOtherIncomeExpense,trailingInterestExpense,trailingOperatingIncome,trailingOperatingExpense,trailingSellingGeneralAndAdministration,trailingResearchAndDevelopment,trailingGrossProfit,trailingCostOfRevenue,trailingTotalRevenue"
      val income_annual = "annualWeighteAverageShare,annualDilutedAverageShares,annualBasicAverageShares,annualearningsPerShare,annualDilutedEPS,annualBasicEPS,annualNetIncomeCommonStockholders,annualNetIncome,annualNetIncomeContinuousOperations,annualTaxProvision,annualPretaxIncome,annualOtherIncomeExpense,annualInterestExpense,annualOperatingIncome,annualOperatingExpense,annualSellingGeneralAndAdministration,annualResearchAndDevelopment,annualGrossProfit,annualCostOfRevenue,annualTotalRevenue"
      val income_quarterly ="quarterlyEbitda,quarterlyWeighteAverageShare,quarterlyDilutedAverageShares,quarterlyBasicAverageShares,quarterlyearningsPerShare,quarterlyDilutedEPS,quarterlyBasicEPS,quarterlyNetIncomeCommonStockholders,quarterlyNetIncome,quarterlyNetIncomeContinuousOperations,quarterlyTaxProvision,quarterlyPretaxIncome,quarterlyOtherIncomeExpense,quarterlyInterestExpense,quarterlyOperatingIncome,quarterlyOperatingExpense,quarterlySellingGeneralAndAdministration,quarterlyResearchAndDevelopment,quarterlyGrossProfit,quarterlyCostOfRevenue,quarterlyTotalRevenueannualEbitda"

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
      Option(parser.parse(r.text).getOrElse(Json.Null))
    }

    def getStockDetails(page:String = ""): Option[Json] =  {
      val url = "https://sg.finance.yahoo.com/quote/GOOGL"+page+"?p=GOOGL"
      val r=requests.get(url)

      val pattern = new Regex("root\\.App\\.main.*}}}};")
      var patternHit = (pattern findFirstIn r.text).getOrElse("")
      patternHit = patternHit.replace("root.App.main = ","").dropRight(1)
      val json: Json = parser.parse(patternHit).getOrElse(Json.Null)
      json.hcursor.downField("context").downField("dispatcher")
        .downField("stores").downField("QuoteSummaryStore").focus
    }

    def getListOfStocks(): Option[String] ={
      var res: Option[String]= None
      var count = 0
      val pattern = new Regex("<a href=\\\"/quote/[a-zA-Z-\\.]*\\?p=[a-zA-Z\\-\\.]*\\\" title=\\\"")
      var loop = true
      while(loop){
        val r=requests.get("https://sg.finance.yahoo.com/screener/predefined/undervalued_large_caps?offset="+(count*100)+"&count=100")
        //println(r.statusCode)
        //println(r.headers("content-type"))

        val stocks = (pattern findAllIn r.text).map(_
          .replaceAll("<a href=\"","sg.finance.yahoo.com")
          .replaceAll("\" title=\"","")).mkString(",")

        if (stocks.length ==0){
          loop = false
        }else{
          if (res.getOrElse("").length > 0){
            res = Option(res.getOrElse("") + "," + Option(stocks))
          }else{
            res = Option(stocks)
          }
        }
        count = count + 1
      }
      res
    }
  }
}
