package finance.scraper.yahoo

import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneOffset}

import com.jayway.jsonpath.JsonPath
import finance.Util
import finance.model.{Stock, StockRating, StockRecommendation}
import io.circe.{Json, parser}
import net.minidev.json.JSONArray

import scala.util.matching.Regex

object testing extends App{
  StockScraper.get("GOOGL")
}

object StockScraper {

  def get(symbol: String): Stock =  {
    val json = crawl(symbol)
    parse(json.getOrElse("").toString)
  }

  private def crawl(symbol: String): Option[Json] =  {
    val url = "https://sg.finance.yahoo.com/quote/%s?p=%s".format(symbol, symbol)
    val r=requests.get(url)
    Util.printLog("getStockDetails url=%s".format(r.url))

    val pattern = new Regex("root\\.App\\.main.*}}}};")
    var patternHit = (pattern findFirstIn r.text).getOrElse("")
    patternHit = patternHit.replace("root.App.main = ","").dropRight(1)
    val json: Json = parser.parse(patternHit).getOrElse(Json.Null)
    json.hcursor.downField("context").downField("dispatcher")
      .downField("stores").downField("QuoteSummaryStore").focus
  }

  private def parse(json: String): Stock = {

    //Stock details
    val symbol = JsonPath.read[String](json, "$.quoteType.symbol")
    val name = JsonPath.read[String](json, "$.quoteType.shortName")
    val industry = JsonPath.read[String](json, "$.summaryProfile.industry")
    val sector = JsonPath.read[String](json, "$.summaryProfile.sector")
    val country = JsonPath.read[String](json, "$.summaryProfile.country")
    val market = JsonPath.read[String](json, "$.quoteType.market")
    val exchange = JsonPath.read[String](json, "$.quoteType.exchange")
    val website = JsonPath.read[String](json, "$.summaryProfile.website")
    val full_time_employees = JsonPath.read[Int](json, "$.summaryProfile.fullTimeEmployees")
    val description = JsonPath.read[String](json, "$.summaryProfile.longBusinessSummary")
    val quote_type = JsonPath.read[String](json, "$.quoteType.quoteType")
    val exchange_timezone_name = JsonPath.read[String](json, "$.quoteType.exchangeTimezoneName")
    val is_esg_populated = JsonPath.read[Boolean](json, "$.quoteType.isEsgPopulated")
    val is_tradeable = JsonPath.read[Boolean](json, "$.summaryDetail.tradeable")

    val stock =
      Stock(symbol, name, industry, sector,
        country, market, exchange, website,
        description, quote_type, exchange_timezone_name, is_esg_populated,
        is_tradeable, full_time_employees
      )

    //Stock ratings
    val rating_histories = JsonPath.read[JSONArray](json, "$.upgradeDowngradeHistory.history")
    rating_histories.forEach(
      rating_history=>{
        val epochGradeDate = JsonPath.read[Int](rating_history, "$.epochGradeDate")
        val graded_timestamp = Timestamp.valueOf(LocalDateTime.ofEpochSecond(epochGradeDate,0, ZoneOffset.UTC))
        val firm = JsonPath.read[String](rating_history, "$.firm")
        val to_grade = JsonPath.read[String](rating_history, "$.toGrade")
        val from_grade = JsonPath.read[String](rating_history, "$.fromGrade")
        val action = JsonPath.read[String](rating_history, "$.action")
        stock.ratings=stock.ratings.appended( StockRating(symbol, firm,from_grade,to_grade, action,graded_timestamp) )
      }
    )


    //Stock ratings
    val stock_recommendations = JsonPath.read[JSONArray](json, "$.recommendationTrend.trend")
    stock_recommendations.forEach(
      stock_recommendation=>{
        val strongBuy = JsonPath.read[Int](stock_recommendation, "$.strongBuy")
        val buy = JsonPath.read[Int](stock_recommendation, "$.buy")
        val hold = JsonPath.read[Int](stock_recommendation, "$.hold")
        val sell = JsonPath.read[Int](stock_recommendation, "$.sell")
        val strongSell = JsonPath.read[Int](stock_recommendation, "$.strongSell")
        val period = JsonPath.read[String](stock_recommendation, "$.period").filter(_.isDigit).toInt
        import java.time.LocalDate
        val firstOfMonth = LocalDate.now.withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond
        val recommended_timestamp = Timestamp.valueOf(LocalDateTime.ofEpochSecond(firstOfMonth,0, ZoneOffset.UTC).minusMonths(period))
        stock.recommendations=stock.recommendations.appended( StockRecommendation(symbol, strongBuy,buy,hold, sell,strongSell,recommended_timestamp) )
      }
    )


    stock
  }
}
