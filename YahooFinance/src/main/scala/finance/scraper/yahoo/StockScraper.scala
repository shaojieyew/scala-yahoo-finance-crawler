package finance.scraper.yahoo

import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneOffset}

import com.jayway.jsonpath.JsonPath
import com.sun.xml.internal.bind.v2.TODO
import finance.Util
import finance.model.{Stock, StockRating, StockRecommendation}
import io.circe.{Json, parser}
import net.minidev.json.JSONArray

import scala.util.matching.Regex

object testing extends App{
  StockScraper.get("AAPL")
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
    val symbol = JsonPath.read(json, "$.quoteType.symbol").toString
    val name = JsonPath.read(json, "$.quoteType.shortName").toString
    val industry = JsonPath.read(json, "$.summaryProfile.industry").toString
    val sector = JsonPath.read(json, "$.summaryProfile.sector").toString
    val country = JsonPath.read(json, "$.summaryProfile.country").toString
    val market = JsonPath.read(json, "$.quoteType.market").toString
    val exchange = JsonPath.read(json, "$.quoteType.exchange").toString
    val website = JsonPath.read(json, "$.summaryProfile.website").toString
    val full_time_employees = JsonPath.read(json, "$.summaryProfile.fullTimeEmployees").toString.toInt
    val description = JsonPath.read(json, "$.summaryProfile.longBusinessSummary").toString
    val quote_type = JsonPath.read(json, "$.quoteType.quoteType").toString
    val exchange_timezone_name = JsonPath.read(json, "$.quoteType.exchangeTimezoneName").toString
    val is_esg_populated = JsonPath.read(json, "$.quoteType.isEsgPopulated").toString.toBoolean
    val is_tradeable = JsonPath.read(json, "$.summaryDetail.tradeable").toString.toBoolean
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

    //TODO, change to option to remove single quote'' empty data
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
