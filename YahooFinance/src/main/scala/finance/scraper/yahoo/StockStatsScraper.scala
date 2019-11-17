package finance.scraper.yahoo

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

import com.jayway.jsonpath.JsonPath
import finance.Util
import finance.model.{Stock, StockAnalysisEstimate, StockEarningEstimate, StockFinancialStats, StockRevenueEstimate, StockShareStats, StockStats, StockValuationStats}
import io.circe.{Json, parser}
import net.minidev.json.JSONArray

import scala.util.matching.Regex


object StockStatsScraperTest extends App{
  StockStatsScraper.get("GOOGL")
}

object StockStatsScraper {

  def get(symbol: String): List [StockStats] =  {
    val json = crawl(symbol)
    if(json.nonEmpty){
      parseStats(json.get.toString)
    }else{
      List()
    }
  }

  def crawl(symbol: String): Option[Json] =  {
    val url = "https://sg.finance.yahoo.com/quote/%s/key-statistics?p=%s".format(symbol, symbol)
    val r=requests.get(url)
    Util.printLog("StockStatisticScraper getStockStatistic url=%s".format(r.url))

    val pattern = new Regex("root\\.App\\.main.*}}}};")
    var patternHit = (pattern findFirstIn r.text).getOrElse("")
    patternHit = patternHit.replace("root.App.main = ","").dropRight(1)
    val json: Json = parser.parse(patternHit).getOrElse(Json.Null)
    json.hcursor.downField("context").downField("dispatcher")
      .downField("stores").downField("QuoteSummaryStore").focus
  }


  def parseStats(json: String): List [StockStats] = {
    var list : List [StockStats] = List()
    val symbol = JsonPath.read[String](json, "$.quoteType.symbol")
    val parsed_json = parser.parse(json).getOrElse(Json.Null)
    val date = Timestamp.valueOf(LocalDateTime.now.minusDays(LocalDateTime.now.getDayOfMonth).truncatedTo(ChronoUnit.DAYS))
    //valuation measures
    val marketCap = parsed_json.hcursor.downField("price").downField("marketCap").get[Long]("raw").toOption
    val enterpriseValue = parsed_json.hcursor.downField("defaultKeyStatistics").downField("enterpriseValue").get[Long]("raw").toOption
    val trailingPE = parsed_json.hcursor.downField("summaryDetail").downField("trailingPE").get[Float]("raw").toOption
    val forwardPE = parsed_json.hcursor.downField("defaultKeyStatistics").downField("forwardPE").get[Float]("raw").toOption
    val pegRatio_5years_expected = parsed_json.hcursor.downField("defaultKeyStatistics").downField("pegRatio").get[Float]("raw").toOption
    val priceToSales_ttm = parsed_json.hcursor.downField("summaryDetail").downField("priceToSalesTrailing12Months").get[Float]("raw").toOption
    val priceToBook_recentQtr = parsed_json.hcursor.downField("defaultKeyStatistics").downField("priceToBook").get[Float]("raw").toOption
    val enterpriseToRevenue = parsed_json.hcursor.downField("defaultKeyStatistics").downField("enterpriseToRevenue").get[Float]("raw").toOption
    val enterpriseToEbitda = parsed_json.hcursor.downField("defaultKeyStatistics").downField("enterpriseToEbitda").get[Float]("raw").toOption
    val beta = parsed_json.hcursor.downField("defaultKeyStatistics").downField("beta").get[Float]("raw").toOption

    list = list.appended(StockValuationStats(symbol, date, marketCap,
      enterpriseValue,
      trailingPE,
      forwardPE,
      pegRatio_5years_expected,
      priceToSales_ttm,
      priceToBook_recentQtr,
      enterpriseToRevenue,
      enterpriseToEbitda,beta
    ))

    //Profitability TTM
    val profitMargins = parsed_json.hcursor.downField("financialData").downField("profitMargins").get[Float]("raw").toOption
    val operatingMargins_ttm = parsed_json.hcursor.downField("financialData").downField("operatingMargins").get[Float]("raw").toOption
    val grossMargins = parsed_json.hcursor.downField("financialData").downField("grossMargins").get[Float]("raw").toOption
    //Management Effectiveness TTM
    val returnOnEquity_ttm = parsed_json.hcursor.downField("financialData").downField("returnOnEquity").get[Float]("raw").toOption
    val returnOnAssets_ttm = parsed_json.hcursor.downField("financialData").downField("returnOnAssets").get[Float]("raw").toOption
    //Income and Balance
    val revenuePerShare_ttm = parsed_json.hcursor.downField("financialData").downField("revenuePerShare").get[Float]("raw").toOption //ttm
    val revenueGrowth_yoy = parsed_json.hcursor.downField("financialData").downField("revenueGrowth").get[Float]("raw").toOption //yoy
    val dilutedEps_ttm = parsed_json.hcursor.downField("defaultKeyStatistics").downField("trailingEps").get[Float]("raw").toOption //ttm
    val earningsQuarterlyGrowth_yoy = parsed_json.hcursor.downField("defaultKeyStatistics").downField("earningsQuarterlyGrowth").get[Float]("raw").toOption //yoy
    val earningsGrowth = parsed_json.hcursor.downField("financialData").downField("earningsGrowth").get[Float]("raw").toOption
    val totalCashPerShare_recentQtr = parsed_json.hcursor.downField("financialData").downField("totalCashPerShare").get[Float]("raw").toOption
    val debtToEquity_recentQtr = parsed_json.hcursor.downField("financialData").downField("debtToEquity").get[Float]("raw").toOption
    val currentRatio_recentQtr = parsed_json.hcursor.downField("financialData").downField("currentRatio").get[Float]("raw").toOption
    val bookValuePerShare_recentQtr = parsed_json.hcursor.downField("defaultKeyStatistics").downField("bookValue").get[Float]("raw").toOption

    list = list.appended(StockFinancialStats(
      symbol,
      date,
      profitMargins ,
      operatingMargins_ttm ,
      grossMargins ,
      returnOnEquity_ttm ,
      returnOnAssets_ttm ,
      revenuePerShare_ttm ,
      revenueGrowth_yoy,
      dilutedEps_ttm,
      earningsQuarterlyGrowth_yoy,
      earningsGrowth,
      totalCashPerShare_recentQtr,
      debtToEquity_recentQtr,
      currentRatio_recentQtr,
      bookValuePerShare_recentQtr
    ))

    //share statistic
    val sharesOutstanding = parsed_json.hcursor.downField("defaultKeyStatistics").downField("sharesOutstanding").get[Long]("raw").toOption
    val floatShares = parsed_json.hcursor.downField("defaultKeyStatistics").downField("floatShares").get[Long]("raw").toOption
    val heldPercentInstitutions = parsed_json.hcursor.downField("defaultKeyStatistics").downField("heldPercentInstitutions").get[Float]("raw").toOption
    val heldPercentInsiders = parsed_json.hcursor.downField("defaultKeyStatistics").downField("heldPercentInsiders").get[Float]("raw").toOption
    val sharesShortPriorMonth = parsed_json.hcursor.downField("defaultKeyStatistics").downField("sharesShortPriorMonth").get[Long]("raw").toOption
    val sharesShort = parsed_json.hcursor.downField("defaultKeyStatistics").downField("sharesShort").get[Long]("raw").toOption
    val shortRatio = parsed_json.hcursor.downField("defaultKeyStatistics").downField("shortRatio").get[Float]("raw").toOption
    val shortPercentOfFloat = parsed_json.hcursor.downField("defaultKeyStatistics").downField("shortPercentOfFloat").get[Float]("raw").toOption
    val sharesPercentSharesOut = parsed_json.hcursor.downField("defaultKeyStatistics").downField("sharesPercentSharesOut").get[Float]("raw").toOption

    list = list.appended(StockShareStats(
      symbol,
      date,
          sharesOutstanding,
          floatShares ,
          heldPercentInstitutions ,
          heldPercentInsiders ,
          sharesShortPriorMonth ,
          sharesShort,
          shortRatio,
          shortPercentOfFloat ,
          sharesPercentSharesOut
    ))
    list
  }
}
