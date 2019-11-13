package finance.query

import doobie.implicits._
import finance.Util
import finance.model.{StockBalance, StockIncome}

object StockBalanceQuery extends Database{

  def insertStockFinanceBalance(stockBalance: StockBalance): Unit ={
    Util.printLog("StockBalanceQuery insertStockFinanceBalance, symbol=%s, date=%s".format(stockBalance.stock, stockBalance.date))
    sql"""

INSERT INTO finance.stock_company_bal(
            stock, date, total_assets, stockholders_equity, gains_losses_not_affecting_retained_earning,
            retained_earning, capital_stock, total_liabilities_net_minority_interest,
            total_non_current_liabilities_net_minority_interest, other_non_current_liabilities,
            non_current_deferred_revenue, non_current_deferred_taxes_liabilities,
            long_term_debt, current_liabilities, other_current_liabilities,
            current_deferred_revenue, current_accrued_expenses, income_tax_payable,
            account_payable, current_debt, total_non_current_assets, other_non_current_assets,
            other_intangible_assets, goodwill, investments_n_advances, net_ppe,
            accumulated_depreciation, gross_ppe, current_assets, other_current_assets,
            inventory, account_receivable, cash_cashequivalents_marketablesecurities,
            other_short_term_investments, cash_cashequivalents, period)

    VALUES (${stockBalance.stock},${stockBalance.date}, ${stockBalance.TotalAssets}, ${stockBalance.StockholdersEquity}, ${stockBalance.GainsLossesNotAffectingRetainedEarnings},
            ${stockBalance.RetainedEarnings}, ${stockBalance.CapitalStock}, ${stockBalance.TotalLiabilitiesNetMinorityInterest},
            ${stockBalance.TotalNonCurrentLiabilitiesNetMinorityInterest}, ${stockBalance.OtherNonCurrentLiabilities},
            ${stockBalance.NonCurrentDeferredRevenue}, ${stockBalance.NonCurrentDeferredTaxesLiabilities},
            ${stockBalance.LongTermDebt}, ${stockBalance.CurrentLiabilities}, ${stockBalance.OtherCurrentLiabilities},
            ${stockBalance.CurrentDeferredRevenue}, ${stockBalance.CurrentAccruedExpenses}, ${stockBalance.IncomeTaxPayable},
            ${stockBalance.AccountsPayable}, ${stockBalance.CurrentDebt}, ${stockBalance.TotalNonCurrentAssets}, ${stockBalance.OtherNonCurrentAssets},
            ${stockBalance.OtherIntangibleAssets}, ${stockBalance.Goodwill}, ${stockBalance.InvestmentsAndAdvances}, ${stockBalance.NetPPE},
            ${stockBalance.AccumulatedDepreciation}, ${stockBalance.GrossPPE}, ${stockBalance.CurrentAssets}, ${stockBalance.OtherCurrentAssets},
            ${stockBalance.Inventory}, ${stockBalance.AccountsReceivable}, ${stockBalance.CashCashEquivalentsAndMarketableSecurities},
            ${stockBalance.OtherShortTermInvestments}, ${stockBalance.CashAndCashEquivalents}, ${stockBalance.period})
    ON CONFLICT (stock, date, period)
    DO
     UPDATE
   SET total_assets=${stockBalance.TotalAssets}, stockholders_equity=${stockBalance.StockholdersEquity}, gains_losses_not_affecting_retained_earning=${stockBalance.GainsLossesNotAffectingRetainedEarnings},
       retained_earning=${stockBalance.RetainedEarnings}, capital_stock=${stockBalance.CapitalStock}, total_liabilities_net_minority_interest=${stockBalance.TotalLiabilitiesNetMinorityInterest},
       total_non_current_liabilities_net_minority_interest=${stockBalance.TotalNonCurrentLiabilitiesNetMinorityInterest}, other_non_current_liabilities=${stockBalance.OtherNonCurrentLiabilities},
       non_current_deferred_revenue=${stockBalance.NonCurrentDeferredRevenue}, non_current_deferred_taxes_liabilities=${stockBalance.NonCurrentDeferredTaxesLiabilities},
       long_term_debt=${stockBalance.LongTermDebt}, current_liabilities=${stockBalance.CurrentLiabilities}, other_current_liabilities=${stockBalance.OtherCurrentLiabilities},
       current_deferred_revenue=${stockBalance.CurrentDeferredRevenue}, current_accrued_expenses=${stockBalance.CurrentAccruedExpenses}, income_tax_payable=${stockBalance.IncomeTaxPayable},
       account_payable=${stockBalance.AccountsPayable}, current_debt=${stockBalance.CurrentDebt}, total_non_current_assets=${stockBalance.TotalNonCurrentAssets},
       other_non_current_assets=${stockBalance.OtherNonCurrentAssets}, other_intangible_assets=${stockBalance.OtherIntangibleAssets}, goodwill=${stockBalance.Goodwill},
       investments_n_advances=${stockBalance.InvestmentsAndAdvances}, net_ppe=${stockBalance.NetPPE}, accumulated_depreciation=${stockBalance.AccumulatedDepreciation},
       gross_ppe=${stockBalance.GrossPPE}, current_assets=${stockBalance.CurrentAssets}, other_current_assets=${stockBalance.OtherCurrentAssets}, inventory=${stockBalance.Inventory},
       account_receivable=${stockBalance.AccountsReceivable}, cash_cashequivalents_marketablesecurities=${stockBalance.CashCashEquivalentsAndMarketableSecurities},
       other_short_term_investments=${stockBalance.OtherShortTermInvestments}, cash_cashequivalents=${stockBalance.CashAndCashEquivalents}
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

