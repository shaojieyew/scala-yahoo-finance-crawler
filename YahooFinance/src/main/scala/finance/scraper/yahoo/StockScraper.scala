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
  StockScraper.get("5TP.SI")
}

object StockScraper {

  def get(symbol: String): Option[Stock] =  {
    val json = crawl(symbol)
    if(json.nonEmpty){
      Option(parse(json.getOrElse("").toString, symbol))
    }else{
      Option.empty
    }
  }

  private def crawl(symbol: String): Option[Json] =  {
    val url = "https://sg.finance.yahoo.com/quote/%s?p=%s".format(symbol, symbol)
    val r=requests.get(url)
    Util.printLog("StockScraper getStockDetails url=%s".format(r.url))
    if((new Regex("loopkup\\?") findFirstIn r.url).isEmpty){
      val pattern = new Regex("root\\.App\\.main.*}}}};")
      var patternHit = (pattern findFirstIn r.text).getOrElse("")
      patternHit = patternHit.replace("root.App.main = ","").dropRight(1)
      val json: Json = parser.parse(patternHit).getOrElse(Json.Null)
      json.hcursor.downField("context").downField("dispatcher")
        .downField("stores").downField("QuoteSummaryStore").focus
    }else{
      Option.empty
    }
  }

  private def parse(jsonString: String, symbol: String): Stock = {
    val json: Json = parser.parse(jsonString).getOrElse(Json.Null)
    var name = json.hcursor.downField("quoteType").get[String]("longName").toOption
    if( name.isEmpty){
      name = json.hcursor.downField("quoteType").get[String]("shortName").toOption
    }
    // val name = json.hcursor.downField("quoteType").get[String]("shortName").toOption
    val industry = json.hcursor.downField("summaryProfile").get[String]("industry").toOption
    val sector = json.hcursor.downField("summaryProfile").get[String]("sector").toOption
    val country = json.hcursor.downField("summaryProfile").get[String]("country").toOption
    val market = json.hcursor.downField("quoteType").get[String]("market").toOption
    val exchange = json.hcursor.downField("quoteType").get[String]("exchange").toOption
    val website = json.hcursor.downField("summaryProfile").get[String]("website").toOption
    val full_time_employees = json.hcursor.downField("summaryProfile").get[Int]("fullTimeEmployees").toOption
    val description = json.hcursor.downField("summaryProfile").get[String]("longBusinessSummary").toOption
    val quote_type = json.hcursor.downField("quoteType").get[String]("quoteType").toOption
    val exchange_timezone_name = json.hcursor.downField("quoteType").get[String]("exchangeTimezoneName").toOption
    val is_esg_populated = json.hcursor.downField("quoteType").get[Boolean]("isEsgPopulated").toOption
    val is_tradeable = json.hcursor.downField("summaryDetail").get[Boolean]("tradeable").toOption

    val stock =
      Stock(symbol, name, industry, sector,
        country, market, exchange, website,
        description, quote_type, exchange_timezone_name, is_esg_populated,
        is_tradeable, full_time_employees
      )

    //Stock ratings

    if(json.hcursor.downField("upgradeDowngradeHistory").downField("history").focus.nonEmpty) {
      json.hcursor.downField("upgradeDowngradeHistory").downField("history").focus.get.asArray.get.foreach(
        rating_history => {
          //println(rating_history)
          val epochGradeDate = rating_history.hcursor.get[Long]("epochGradeDate").toOption
          val graded_timestamp = Timestamp.valueOf(LocalDateTime.ofEpochSecond(epochGradeDate.getOrElse(0), 0, ZoneOffset.UTC))
          val firm = rating_history.hcursor.get[String]("firm").toOption
          val to_grade = rating_history.hcursor.get[String]("toGrade").toOption
          val from_grade = rating_history.hcursor.get[String]("fromGrade").toOption
          val action = rating_history.hcursor.get[String]("action").toOption
          stock.ratings = stock.ratings.appended(StockRating(symbol, firm, from_grade, to_grade, action, graded_timestamp))
        }
      )
    }

    //TODO, change to option to remove single quote'' empty data
    //Stock ratings
    if(json.hcursor.downField("recommendationTrend").downField("trend").focus.nonEmpty){
      json.hcursor.downField("recommendationTrend").downField("trend").focus.get.asArray.get.foreach(
        stock_recommendation=>{

          val strongBuy = stock_recommendation.hcursor.get[Int]("strongBuy").toOption
          val buy = stock_recommendation.hcursor.get[Int]("buy").toOption
          val hold = stock_recommendation.hcursor.get[Int]("hold").toOption
          val sell = stock_recommendation.hcursor.get[Int]("sell").toOption
          val strongSell = stock_recommendation.hcursor.get[Int]("strongSell").toOption
          val period = stock_recommendation.hcursor.get[String]("period").getOrElse("").filter(_.isDigit).toInt


          import java.time.LocalDate
          val firstOfMonth = LocalDate.now.withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond
          val recommended_timestamp = Timestamp.valueOf(LocalDateTime.ofEpochSecond(firstOfMonth,0, ZoneOffset.UTC).minusMonths(period))
          stock.recommendations=stock.recommendations.appended( StockRecommendation(symbol, strongBuy,buy,hold, sell,strongSell,recommended_timestamp) )
        }
      )
    }



    stock
  }
}
