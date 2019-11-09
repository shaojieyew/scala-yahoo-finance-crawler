package finance.model

import java.sql.Timestamp

import finance.Util
import finance.query.{StockBalanceQuery, StockCashFlowQuery, StockIncomeQuery}
import finance.scraper.yahoo.StockFinanceScraper



  abstract class StockFinance{
    def updateInstance(value: Float, finance_type: String)
    def debugVars():Any = {
      val vars = this.getClass.getDeclaredFields
      for(v <- vars){
        v.setAccessible(true)
        Util.printLog("Field: " + v.getName() + " => " + v.get(this))
      }
    }
  }
  class StockIncome(val stock: String, val date: Timestamp, val period: String, val currency: String , var Ebitda: Option[Float] = Option.empty,
                    var WeighteAverageShare: Option[Float] = Option.empty,var DilutedAverageShares: Option[Float] = Option.empty,
                    var BasicAverageShares: Option[Float] = Option.empty,
                    var earningsPerShare: Option[Float] = Option.empty,var DilutedEPS: Option[Float] = Option.empty,
                    var BasicEPS: Option[Float] = Option.empty, var NetIncomeCommonStockholders: Option[Float] = Option.empty,
                    var NetIncome: Option[Float] = Option.empty,
                    var NetIncomeContinuousOperations: Option[Float] = Option.empty,var TaxProvision: Option[Float] = Option.empty,
                    var PretaxIncome: Option[Float] = Option.empty,var OtherIncomeExpense: Option[Float] = Option.empty,
                    var InterestExpense: Option[Float] = Option.empty, var OperatingIncome: Option[Float] = Option.empty,
                    var  OperatingExpense: Option[Float] = Option.empty,var SellingGeneralAndAdministration: Option[Float] = Option.empty,
                    var  ResearchAndDevelopment: Option[Float] = Option.empty,var GrossProfit: Option[Float] = Option.empty,
                    var  CostOfRevenue: Option[Float] = Option.empty, var TotalRevenue: Option[Float] = Option.empty) extends StockFinance{

    def updateInstance(value:Float, finance_type: String) : Unit = {
      finance_type match {
        case "Ebitda" => Ebitda=Option(value)
        case "WeighteAverageShare" => WeighteAverageShare=Option(value)
        case "DilutedAverageShares" => DilutedAverageShares=Option(value)
        case "BasicAverageShares" => BasicAverageShares=Option(value)
        case "earningsPerShare" => earningsPerShare=Option(value)
        case "DilutedEPS" => DilutedEPS=Option(value)
        case "BasicEPS" => BasicEPS=Option(value)
        case "NetIncomeCommonStockholders" => NetIncomeCommonStockholders=Option(value)
        case "NetIncome" => NetIncome=Option(value)
        case "NetIncomeContinuousOperations" => NetIncomeContinuousOperations=Option(value)
        case "TaxProvision" => TaxProvision=Option(value)
        case "PretaxIncome" => PretaxIncome=Option(value)
        case "OtherIncomeExpense" => OtherIncomeExpense=Option(value)
        case "InterestExpense" => InterestExpense=Option(value)
        case "OperatingIncome" => OperatingIncome=Option(value)
        case "OperatingExpense" => OperatingExpense=Option(value)
        case "SellingGeneralAndAdministration" => SellingGeneralAndAdministration=Option(value)
        case "ResearchAndDevelopment" => ResearchAndDevelopment=Option(value)
        case "GrossProfit" => GrossProfit=Option(value)
        case "CostOfRevenue" => CostOfRevenue=Option(value)
        case "TotalRevenue" => TotalRevenue=Option(value)
      }
    }
  }


class StockBalance(val stock: String,val date: Timestamp, val period: String, val currency: String , var TotalAssets: Option[Float] = Option.empty,
                   var StockholdersEquity: Option[Float] = Option.empty,
                   var GainsLossesNotAffectingRetainedEarnings: Option[Float] = Option.empty,
                   var RetainedEarnings: Option[Float] = Option.empty,
                   var CapitalStock: Option[Float] = Option.empty,
                   var TotalLiabilitiesNetMinorityInterest: Option[Float] = Option.empty,
                   var TotalNonCurrentLiabilitiesNetMinorityInterest: Option[Float] = Option.empty,
                   var OtherNonCurrentLiabilities: Option[Float] = Option.empty,
                   var NonCurrentDeferredRevenue: Option[Float] = Option.empty,
                   var NonCurrentDeferredTaxesLiabilities: Option[Float] = Option.empty,
                   var LongTermDebt: Option[Float] = Option.empty,
                   var CurrentLiabilities: Option[Float] = Option.empty,
                   var OtherCurrentLiabilities: Option[Float] = Option.empty,
                   var CurrentDeferredRevenue: Option[Float] = Option.empty,
                   var CurrentAccruedExpenses: Option[Float] = Option.empty,
                   var IncomeTaxPayable: Option[Float] = Option.empty,
                   var AccountsPayable: Option[Float] = Option.empty,
                   var CurrentDebt: Option[Float] = Option.empty,
                   var TotalNonCurrentAssets: Option[Float] = Option.empty,
                   var OtherNonCurrentAssets: Option[Float] = Option.empty,
                   var OtherIntangibleAssets: Option[Float] = Option.empty,
                   var Goodwill: Option[Float] = Option.empty,
                   var InvestmentsAndAdvances: Option[Float] = Option.empty,
                   var NetPPE: Option[Float] = Option.empty,
                   var AccumulatedDepreciation: Option[Float] = Option.empty,
                   var GrossPPE: Option[Float] = Option.empty,
                   var CurrentAssets: Option[Float] = Option.empty,
                   var OtherCurrentAssets: Option[Float] = Option.empty,
                   var Inventory: Option[Float] = Option.empty,
                   var AccountsReceivable: Option[Float] = Option.empty,
                   var CashCashEquivalentsAndMarketableSecurities: Option[Float] = Option.empty,
                   var OtherShortTermInvestments: Option[Float] = Option.empty,
                   var CashAndCashEquivalents: Option[Float] = Option.empty ) extends StockFinance{

  def updateInstance(value:Float, finance_type: String) : Unit = {
    finance_type match {
      case "TotalAssets" => TotalAssets=Option(value)
      case "StockholdersEquity" => StockholdersEquity=Option(value)
      case "GainsLossesNotAffectingRetainedEarnings" => GainsLossesNotAffectingRetainedEarnings=Option(value)
      case "RetainedEarnings" => RetainedEarnings=Option(value)
      case "CapitalStock" => CapitalStock=Option(value)
      case "TotalLiabilitiesNetMinorityInterest" => TotalLiabilitiesNetMinorityInterest=Option(value)
      case "TotalNonCurrentLiabilitiesNetMinorityInterest" => TotalNonCurrentLiabilitiesNetMinorityInterest=Option(value)
      case "OtherNonCurrentLiabilities" => OtherNonCurrentLiabilities=Option(value)
      case "NonCurrentDeferredRevenue" => NonCurrentDeferredRevenue=Option(value)
      case "NonCurrentDeferredTaxesLiabilities" => NonCurrentDeferredTaxesLiabilities=Option(value)
      case "LongTermDebt" => LongTermDebt=Option(value)
      case "CurrentLiabilities" => CurrentLiabilities=Option(value)
      case "OtherCurrentLiabilities" => OtherCurrentLiabilities=Option(value)
      case "CurrentDeferredRevenue" => CurrentDeferredRevenue=Option(value)
      case "CurrentAccruedExpenses" => CurrentAccruedExpenses=Option(value)
      case "IncomeTaxPayable" => IncomeTaxPayable=Option(value)
      case "AccountsPayable" => AccountsPayable=Option(value)
      case "CurrentDebt" => CurrentDebt=Option(value)
      case "TotalNonCurrentAssets" => TotalNonCurrentAssets=Option(value)
      case "OtherNonCurrentAssets" => OtherNonCurrentAssets=Option(value)
      case "OtherIntangibleAssets" => OtherIntangibleAssets=Option(value)
      case "Goodwill" => Goodwill=Option(value)
      case "InvestmentsAndAdvances" => InvestmentsAndAdvances=Option(value)
      case "NetPPE" => NetPPE=Option(value)
      case "AccumulatedDepreciation" => AccumulatedDepreciation=Option(value)
      case "GrossPPE" => GrossPPE=Option(value)
      case "CurrentAssets" => CurrentAssets=Option(value)
      case "OtherCurrentAssets" => OtherCurrentAssets=Option(value)
      case "Inventory" => Inventory=Option(value)
      case "AccountsReceivable" => AccountsReceivable=Option(value)
      case "CashCashEquivalentsAndMarketableSecurities" => CashCashEquivalentsAndMarketableSecurities=Option(value)
      case "OtherShortTermInvestments" => OtherShortTermInvestments=Option(value)
      case "CashAndCashEquivalents" => CashAndCashEquivalents=Option(value)
    }
  }
}




class StockCashFlow(val stock: String, val date: Timestamp, val period: String, val currency: String , var FreeCashFlow: Option[Float] = Option.empty,
                    var CapitalExpenditure: Option[Float] = Option.empty,
                    var OperatingCashFlow: Option[Float] = Option.empty,
                    var EndCashPosition: Option[Float] = Option.empty,
                    var BeginningCashPosition: Option[Float] = Option.empty,

                    var ChangeInCashSupplementalAsReported: Option[Float] = Option.empty,
                    var CashFlowFromContinuingFinancingActivities: Option[Float] = Option.empty,
                    var NetOtherFinancingCharges: Option[Float] = Option.empty,

                    var CashDividendsPaid: Option[Float] = Option.empty,
                    var RepurchaseOfCapitalStock: Option[Float] = Option.empty,
                    var CommonStockIssuance: Option[Float] = Option.empty,
                    var RepaymentOfDebt: Option[Float] = Option.empty,
                    var InvestingCashFlow: Option[Float] = Option.empty,

                    var NetOtherInvestingChanges: Option[Float] = Option.empty,
                    var SaleOfInvestment: Option[Float] = Option.empty,
                    var PurchaseOfInvestment: Option[Float] = Option.empty,
                    var PurchaseOfBusiness: Option[Float] = Option.empty,
                    var OtherNonCashItems: Option[Float] = Option.empty,

                    var ChangeInAccountPayable: Option[Float] = Option.empty,
                    var ChangeInInventory: Option[Float] = Option.empty,
                    var ChangesInAccountReceivables: Option[Float] = Option.empty,
                    var ChangeInWorkingCapital: Option[Float] = Option.empty,
                    var StockBasedCompensation: Option[Float] = Option.empty,

                    var DeferredIncomeTax: Option[Float] = Option.empty,
                    var DepreciationAndAmortization: Option[Float] = Option.empty,
                    var NetIncome: Option[Float] = Option.empty) extends StockFinance{


  def updateInstance(value:Float, finance_type: String) : Unit = {
    finance_type match {
      case "FreeCashFlow" => FreeCashFlow=Option(value)
      case "CapitalExpenditure" => CapitalExpenditure=Option(value)
      case "OperatingCashFlow" => OperatingCashFlow=Option(value)
      case "EndCashPosition" => EndCashPosition=Option(value)
      case "BeginningCashPosition" => BeginningCashPosition=Option(value)

      case "ChangeInCashSupplementalAsReported" => ChangeInCashSupplementalAsReported=Option(value)
      case "CashFlowFromContinuingFinancingActivities" => CashFlowFromContinuingFinancingActivities=Option(value)
      case "NetOtherFinancingCharges" => NetOtherFinancingCharges=Option(value)

      case "CashDividendsPaid" => CashDividendsPaid=Option(value)
      case "RepurchaseOfCapitalStock" => RepurchaseOfCapitalStock=Option(value)
      case "CommonStockIssuance" => CommonStockIssuance=Option(value)
      case "RepaymentOfDebt" => RepaymentOfDebt=Option(value)
      case "InvestingCashFlow" => InvestingCashFlow=Option(value)

      case "NetOtherInvestingChanges" => NetOtherInvestingChanges=Option(value)
      case "SaleOfInvestment" => SaleOfInvestment=Option(value)
      case "PurchaseOfInvestment" => PurchaseOfInvestment=Option(value)
      case "PurchaseOfBusiness" => PurchaseOfBusiness=Option(value)
      case "OtherNonCashItems" => OtherNonCashItems=Option(value)

      case "ChangeInAccountPayable" => ChangeInAccountPayable=Option(value)
      case "ChangeInInventory" => ChangeInInventory=Option(value)
      case "ChangesInAccountReceivables" => ChangesInAccountReceivables=Option(value)
      case "ChangeInWorkingCapital" => ChangeInWorkingCapital=Option(value)
      case "StockBasedCompensation" => StockBasedCompensation=Option(value)

      case "DeferredIncomeTax" => DeferredIncomeTax=Option(value)
      case "DepreciationAndAmortization" => DepreciationAndAmortization=Option(value)
      case "NetIncome" => NetIncome=Option(value)
    }
  }
}






object StockFinance{
    val TYPES = Array("trailing","annual","quarterly")
    val INCOME_COLS = List("Ebitda","WeighteAverageShare","DilutedAverageShares","BasicAverageShares",
      "earningsPerShare","DilutedEPS","BasicEPS","NetIncomeCommonStockholders","NetIncome",
      "NetIncomeContinuousOperations","TaxProvision","PretaxIncome","OtherIncomeExpense",
      "InterestExpense","OperatingIncome","OperatingExpense","SellingGeneralAndAdministration",
      "ResearchAndDevelopment","GrossProfit","CostOfRevenue","TotalRevenue")

    val BALANCE_COLS = List("TotalAssets","StockholdersEquity","GainsLossesNotAffectingRetainedEarnings","RetainedEarnings",
      "CapitalStock","TotalLiabilitiesNetMinorityInterest","TotalNonCurrentLiabilitiesNetMinorityInterest",
      "OtherNonCurrentLiabilities","NonCurrentDeferredRevenue","NonCurrentDeferredTaxesLiabilities",
      "LongTermDebt","CurrentLiabilities","OtherCurrentLiabilities","CurrentDeferredRevenue","CurrentAccruedExpenses",
      "IncomeTaxPayable","AccountsPayable","CurrentDebt","TotalNonCurrentAssets","OtherNonCurrentAssets",
      "OtherIntangibleAssets","Goodwill","InvestmentsAndAdvances","NetPPE","AccumulatedDepreciation","GrossPPE",
      "CurrentAssets","OtherCurrentAssets","Inventory","AccountsReceivable","CashCashEquivalentsAndMarketableSecurities",
      "OtherShortTermInvestments","CashAndCashEquivalents")

    val CASHFLOW_COLS = List("FreeCashFlow","CapitalExpenditure","OperatingCashFlow","EndCashPosition","BeginningCashPosition",
      "ChangeInCashSupplementalAsReported","CashFlowFromContinuingFinancingActivities","NetOtherFinancingCharges",
      "CashDividendsPaid","RepurchaseOfCapitalStock","CommonStockIssuance","RepaymentOfDebt","InvestingCashFlow",
      "NetOtherInvestingChanges","SaleOfInvestment","PurchaseOfInvestment","PurchaseOfBusiness","OtherNonCashItems",
      "ChangeInAccountPayable","ChangeInInventory","ChangesInAccountReceivables","ChangeInWorkingCapital","StockBasedCompensation",
      "DeferredIncomeTax","DepreciationAndAmortization","NetIncome")
    def getInstance(stock:String, date: Timestamp, period: String, value: Float,  finance_type:String, currency: String = "USD"): StockFinance = {
      var stockFinance: StockFinance = {
        if(INCOME_COLS.contains(finance_type)){
          StockIncome.getInstance(stock, date, period, value, finance_type, currency)
        }else{
          if(BALANCE_COLS.contains(finance_type)){
            StockBalance.getInstance(stock, date, period, value, finance_type, currency)
          }else{
            StockCashFlow.getInstance(stock, date, period, value, finance_type, currency)
          }
        }
      }
      stockFinance
    }


  def insertUpdateStockFinance(symbol:String): Unit ={
    StockFinanceScraper.get(symbol).foreach(stockFinance=>{
      if(stockFinance.getClass.getName==StockIncome.getClass.getName.dropRight(1)){
        StockIncomeQuery.insertStockFinanceIncome(stockFinance.asInstanceOf[StockIncome])
      }
      if(stockFinance.getClass.getName==StockBalance.getClass.getName.dropRight(1)){
        StockBalanceQuery.insertStockFinanceBalance(stockFinance.asInstanceOf[StockBalance])
      }
      if(stockFinance.getClass.getName==StockCashFlow.getClass.getName.dropRight(1)){
        StockCashFlowQuery.insertStockFinanceCashflow(stockFinance.asInstanceOf[StockCashFlow])
      }
    })
  }
}

object StockIncome extends{
    def getInstance(stock:String, date: Timestamp, period: String,value: Float,finance_type:String, currency: String = "USD" ): StockIncome = {
      val s = new StockIncome(stock, date, period, currency)
      s.updateInstance(value, finance_type)
      s
    }
  }
object StockBalance extends{
  def getInstance(stock:String, date: Timestamp, period: String,value: Float,finance_type:String, currency: String = "USD" ): StockBalance = {
    val s = new StockBalance(stock, date, period, currency)
    s.updateInstance(value, finance_type)
    s
  }
}
object StockCashFlow extends{
  def getInstance(stock:String, date: Timestamp, period: String,value: Float,finance_type:String, currency: String = "USD" ): StockCashFlow = {
    val s = new StockCashFlow(stock, date, period, currency)
    s.updateInstance(value, finance_type)
    s
  }
}