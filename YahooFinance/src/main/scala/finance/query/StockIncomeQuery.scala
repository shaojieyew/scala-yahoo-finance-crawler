package finance.query

import doobie.implicits._
import finance.Util
import finance.model.{StockIncome, StockPrice}

object StockIncomeQuery extends Database{

  def insertStockFinanceIncome(stockIncome: StockIncome): Unit ={
    Util.printLog("StockIncomeQuery insertStockFinanceIncome, symbol=%s, date=%s".format(stockIncome.stock, stockIncome.date))
    sql"""
    INSERT INTO finance.stock_company_income(
            stock, date, weighted_avg_share, diluted_avg_share, basic_avg_share,
            earning_per_share, diluted_eps, basic_eps, net_income_common_stock_holder,
            net_income, net_income_continuous_ops, tax_provision, pre_tax_income,
            other_income_expense, interest_expense, operating_income, operating_expense,
            selling_general_n_admin, research_and_development, gross_profit,
            cost_of_revenue, total_revenue, period)
    VALUES (${stockIncome.stock}, ${stockIncome.date}, ${stockIncome.WeighteAverageShare}, ${stockIncome.DilutedAverageShares}, ${stockIncome.BasicAverageShares},
            ${stockIncome.earningsPerShare}, ${stockIncome.DilutedEPS}, ${stockIncome.BasicEPS}, ${stockIncome.NetIncomeCommonStockholders},
            ${stockIncome.NetIncome}, ${stockIncome.NetIncomeContinuousOperations}, ${stockIncome.TaxProvision}, ${stockIncome.PretaxIncome},
            ${stockIncome.OtherIncomeExpense}, ${stockIncome.InterestExpense}, ${stockIncome.OperatingIncome}, ${stockIncome.OperatingExpense},
            ${stockIncome.SellingGeneralAndAdministration}, ${stockIncome.ResearchAndDevelopment}, ${stockIncome.GrossProfit},
            ${stockIncome.CostOfRevenue}, ${stockIncome.TotalRevenue}, ${stockIncome.period})
    ON CONFLICT (stock, date, period)
    DO
      UPDATE
   SET weighted_avg_share=${stockIncome.WeighteAverageShare}, diluted_avg_share=${stockIncome.DilutedAverageShares}, basic_avg_share=${stockIncome.BasicAverageShares},
       earning_per_share=${stockIncome.earningsPerShare}, diluted_eps=${stockIncome.DilutedEPS}, basic_eps= ${stockIncome.BasicEPS}, net_income_common_stock_holder=${stockIncome.NetIncomeCommonStockholders},
       net_income=${stockIncome.NetIncome}, net_income_continuous_ops=${stockIncome.NetIncomeContinuousOperations}, tax_provision= ${stockIncome.TaxProvision}, pre_tax_income=${stockIncome.PretaxIncome},
       other_income_expense=${stockIncome.OtherIncomeExpense}, interest_expense=${stockIncome.InterestExpense}, operating_income=${stockIncome.OperatingIncome},
       operating_expense=${stockIncome.OperatingExpense}, selling_general_n_admin=${stockIncome.SellingGeneralAndAdministration}, research_and_development=${stockIncome.ResearchAndDevelopment},
       gross_profit=${stockIncome.GrossProfit}, cost_of_revenue=${stockIncome.CostOfRevenue}, total_revenue= ${stockIncome.TotalRevenue};
      """
      .update
      .run.transact(xa).unsafeRunSync
  }

}

