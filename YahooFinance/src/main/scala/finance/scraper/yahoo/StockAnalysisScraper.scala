package finance.scraper.yahoo

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

import com.jayway.jsonpath.JsonPath
import finance.Util
import finance.model.{Stock, StockAnalysisEstimate, StockEarningEstimate, StockRevenueEstimate}
import io.circe.{Json, parser}
import net.minidev.json.JSONArray

import scala.util.matching.Regex


object test222 extends App{
  StockAnalysisScraper.get("AAPL")
}

object StockAnalysisScraper {

  def get(symbol: String): List[StockAnalysisEstimate] =  {
    val json = crawl(symbol)
    parseRevenueEstimate(json.getOrElse("").toString) ::: parseEarningEstimate(json.getOrElse("").toString)
  }

  def crawl(symbol: String): Option[Json] =  {
    val url = "https://sg.finance.yahoo.com/quote/%s/analysis?p=%s".format(symbol, symbol)
    val r=requests.get(url)
    Util.printLog("getStockDetails url=%s".format(r.url))

    val pattern = new Regex("root\\.App\\.main.*}}}};")
    var patternHit = (pattern findFirstIn r.text).getOrElse("")
    patternHit = patternHit.replace("root.App.main = ","").dropRight(1)
    val json: Json = parser.parse(patternHit).getOrElse(Json.Null)
    json.hcursor.downField("context").downField("dispatcher")
      .downField("stores").downField("QuoteSummaryStore").focus
  }


  def parseRevenueEstimate(json: String): List[StockAnalysisEstimate] = {
    val symbol = JsonPath.read[String](json, "$.quoteType.symbol")
    var list: List[StockAnalysisEstimate] = List()
    val parsed_json = parser.parse(json)
    if(parsed_json.getOrElse(Json.Null).hcursor.downField("earningsTrend").downField("trend").focus.nonEmpty) {
      parsed_json.getOrElse(Json.Null).hcursor
       .downField("earningsTrend")
       .downField("trend").focus.get.asArray.get.foreach(rec=>{
       val endDate = rec.hcursor.get[String]("endDate")
       if(endDate.toOption.nonEmpty) {
         val sdf = new SimpleDateFormat("yyyy-MM-dd")
         val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond((sdf.parse(endDate.getOrElse("")).getTime) / 1000L, 0, ZoneOffset.of("+08:00")))
         val estDate = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS))
         val period = rec.hcursor.get[String]("period").getOrElse("-").takeRight(1)
         val avg = rec.hcursor.downField("revenueEstimate").downField("avg").get[Long]("raw").toOption
         val low = rec.hcursor.downField("revenueEstimate").downField("low").get[Long]("raw").toOption
         val high = rec.hcursor.downField("revenueEstimate").downField("high").get[Long]("raw").toOption
         val no_of_analysis = rec.hcursor.downField("revenueEstimate").downField("numberOfAnalysts").get[Int]("raw").toOption
         val growth = rec.hcursor.downField("revenueEstimate").downField("growth").get[Float]("raw").toOption

         /*
          println(date)
         println(estDate)
         println(period)
         println(avg)
         println(low)
         println(high)
         println(no_of_analysis)
         println(growth)
          */
         list=list.appended(StockRevenueEstimate(symbol ,estDate, period, date, avg, low, high, no_of_analysis, growth))

       }
       })
    }
    list
  }

  def parseEarningEstimate(json: String): List[StockAnalysisEstimate] = {
    val symbol = JsonPath.read[String](json, "$.quoteType.symbol")
    var list: List[StockAnalysisEstimate] = List()
    val parsed_json = parser.parse(json)
    if(parsed_json.getOrElse(Json.Null).hcursor.downField("earningsTrend").downField("trend").focus.nonEmpty) {
      parsed_json.getOrElse(Json.Null).hcursor
        .downField("earningsTrend")
        .downField("trend").focus.get.asArray.get.foreach(rec => {
        val endDate = rec.hcursor.get[String]("endDate")
        if (endDate.toOption.nonEmpty) {
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond((sdf.parse(endDate.getOrElse("")).getTime) / 1000L, 0, ZoneOffset.of("+08:00")))
          val estDate = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS))
          val period = rec.hcursor.get[String]("period").getOrElse("-").takeRight(1)
          val avg = rec.hcursor.downField("earningsEstimate").downField("avg").get[Float]("raw").toOption
          val low = rec.hcursor.downField("earningsEstimate").downField("low").get[Float]("raw").toOption
          val high = rec.hcursor.downField("earningsEstimate").downField("high").get[Float]("raw").toOption
          val no_of_analysis = rec.hcursor.downField("earningsEstimate").downField("numberOfAnalysts").get[Int]("raw").toOption
          val growth = rec.hcursor.downField("earningsEstimate").downField("growth").get[Float]("raw").toOption

          val estDate7 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(7))
          val avg7 = rec.hcursor.downField("epsTrend").downField("7daysAgo").get[Float]("raw").toOption


          val estDate30 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(30))
          val avg30 = rec.hcursor.downField("epsTrend").downField("30daysAgo").get[Float]("raw").toOption

          val estDate60 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(60))
          val avg60 = rec.hcursor.downField("epsTrend").downField("60daysAgo").get[Float]("raw").toOption

          val estDate90 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(90))
          val avg90 = rec.hcursor.downField("epsTrend").downField("90daysAgo").get[Float]("raw").toOption

          list = list.appended(StockEarningEstimate(symbol, estDate, period, date, avg, low, high, no_of_analysis, growth))
          list = list.appended(StockEarningEstimate(symbol, estDate7, period, date, avg7, low, high, no_of_analysis, growth))
          list = list.appended(StockEarningEstimate(symbol, estDate30, period, date, avg30, low, high, no_of_analysis, growth))
          list = list.appended(StockEarningEstimate(symbol, estDate60, period, date, avg60, low, high, no_of_analysis, growth))
          list = list.appended(StockEarningEstimate(symbol, estDate90, period, date, avg90, low, high, no_of_analysis, growth))
        }
      })
    }
    list
  }
}
