package finance.query

import doobie.implicits._
import finance.Util
import finance.model.{StockCashFlow, StockIncome}

object StockCashFlowQuery extends Database{

  def insertStockFinanceCashflow(stockCashflow: StockCashFlow): Unit ={
    Util.printLog("StockCashFlowQuery insertStockFinanceCashflow, symbol=%s, date=%s".format(stockCashflow.stock, stockCashflow.date))
    sql"""
    INSERT INTO finance.stock_company_cashflow(
            stock, date, free_cash_flow, capital_expenditure, operating_cashflow,
            end_cash_position, begin_cash_position, change_in_cash_supplemental_reported,
            cash_flow_from_continuing_financing_activities, net_other_financing_charges,
            cash_dividends_paid, repurchase_of_capitalstock, common_stock_issuance,
            repayment_of_debt, investing_cashflow, net_other_investing_changes,
            sale_of_investment, purchase_of_investment, purchase_of_business,
            other_non_cashitems, change_in_account_payable, change_in_inventory,
            changes_in_account_receivables, change_in_working_capital, stock_based_compensation,
            deferred_income_tax, depreciation_amortization, net_income, period)
    VALUES (${stockCashflow.stock}, ${stockCashflow.date}, ${stockCashflow.FreeCashFlow}, ${stockCashflow.CapitalExpenditure}, ${stockCashflow.OperatingCashFlow},
            ${stockCashflow.EndCashPosition}, ${stockCashflow.BeginningCashPosition}, ${stockCashflow.ChangeInCashSupplementalAsReported},
            ${stockCashflow.CashFlowFromContinuingFinancingActivities}, ${stockCashflow.NetOtherFinancingCharges},
            ${stockCashflow.CashDividendsPaid}, ${stockCashflow.RepurchaseOfCapitalStock}, ${stockCashflow.CommonStockIssuance},
            ${stockCashflow.RepaymentOfDebt}, ${stockCashflow.InvestingCashFlow}, ${stockCashflow.NetOtherInvestingChanges},
            ${stockCashflow.SaleOfInvestment}, ${stockCashflow.PurchaseOfInvestment}, ${stockCashflow.PurchaseOfBusiness},
            ${stockCashflow.OtherNonCashItems}, ${stockCashflow.ChangeInAccountPayable}, ${stockCashflow.ChangeInInventory},
            ${stockCashflow.ChangesInAccountReceivables}, ${stockCashflow.ChangeInWorkingCapital}, ${stockCashflow.StockBasedCompensation},
            ${stockCashflow.DeferredIncomeTax}, ${stockCashflow.DepreciationAndAmortization}, ${stockCashflow.NetIncome}, ${stockCashflow.period})
    ON CONFLICT (stock, date, period)
    DO
      UPDATE
   SET free_cash_flow=${stockCashflow.FreeCashFlow}, capital_expenditure=${stockCashflow.CapitalExpenditure}, operating_cashflow=${stockCashflow.OperatingCashFlow},
       end_cash_position=${stockCashflow.EndCashPosition}, begin_cash_position=${stockCashflow.BeginningCashPosition}, change_in_cash_supplemental_reported=${stockCashflow.ChangeInCashSupplementalAsReported},
       cash_flow_from_continuing_financing_activities=${stockCashflow.CashFlowFromContinuingFinancingActivities}, net_other_financing_charges=${stockCashflow.NetOtherFinancingCharges},
       cash_dividends_paid=${stockCashflow.CashDividendsPaid}, repurchase_of_capitalstock=${stockCashflow.RepurchaseOfCapitalStock}, common_stock_issuance=${stockCashflow.CommonStockIssuance},
       repayment_of_debt=${stockCashflow.RepaymentOfDebt}, investing_cashflow=${stockCashflow.InvestingCashFlow}, net_other_investing_changes=${stockCashflow.NetOtherInvestingChanges},
       sale_of_investment=${stockCashflow.SaleOfInvestment}, purchase_of_investment=${stockCashflow.PurchaseOfInvestment}, purchase_of_business=${stockCashflow.PurchaseOfBusiness},
       other_non_cashitems=${stockCashflow.OtherNonCashItems}, change_in_account_payable=${stockCashflow.ChangeInAccountPayable}, change_in_inventory=${stockCashflow.ChangeInInventory},
       changes_in_account_receivables=${stockCashflow.ChangesInAccountReceivables}, change_in_working_capital=${stockCashflow.ChangeInWorkingCapital},
       stock_based_compensation=${stockCashflow.StockBasedCompensation}, deferred_income_tax=${stockCashflow.DeferredIncomeTax}, depreciation_amortization=${stockCashflow.DepreciationAndAmortization},
       net_income=${stockCashflow.NetIncome}
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

