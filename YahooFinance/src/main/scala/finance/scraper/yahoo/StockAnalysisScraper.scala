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
  StockAnalysisScraper.get("GOOGL")
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
    JsonPath.read[JSONArray](json, "$.earningsTrend.trend").forEach(trend =>{
      val endDate = Option(JsonPath.read[String](trend, "$.endDate"))
      if(endDate.nonEmpty){
        val sdf = new SimpleDateFormat("yyyy-MM-dd")
        val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond(sdf.parse(endDate.get).getTime/1000L,0, ZoneOffset.UTC))
        val estDate = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS))
        val period=  JsonPath.read(trend, "$.period").toString.takeRight(1)
        val avg = JsonPath.read(trend, "$.revenueEstimate.avg.raw").toString.toLong
        val low = JsonPath.read(trend, "$.revenueEstimate.low.raw").toString.toLong
        val high = JsonPath.read(trend, "$.revenueEstimate.high.raw").toString.toLong
        val no_of_analysis = JsonPath.read(trend, "$.revenueEstimate.numberOfAnalysts.raw").toString.toInt
        val growth = JsonPath.read(trend, "$.revenueEstimate.growth.raw").toString.toFloat

        list=list.appended(StockRevenueEstimate(symbol ,estDate, period, date, avg, low, high, no_of_analysis, growth))
      }
    })
    list
  }

  def parseEarningEstimate(json: String): List[StockAnalysisEstimate] = {
    val symbol = JsonPath.read[String](json, "$.quoteType.symbol")
    var list: List[StockAnalysisEstimate] = List()
    JsonPath.read[JSONArray](json, "$.earningsTrend.trend").forEach(trend =>{
      val endDate = Option(JsonPath.read[String](trend, "$.endDate"))
      if(endDate.nonEmpty){
        val estDate = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS))
        val period=  JsonPath.read(trend, "$.period").toString.takeRight(1)
        val avg = JsonPath.read(trend, "$.earningsEstimate.avg.raw").toString.toFloat
        val low = JsonPath.read(trend, "$.earningsEstimate.low.raw").toString.toFloat
        val high = JsonPath.read(trend, "$.earningsEstimate.high.raw").toString.toFloat
        val no_of_analysis = JsonPath.read(trend, "$.earningsEstimate.numberOfAnalysts.raw").toString.toInt
        val growth = JsonPath.read(trend, "$.earningsEstimate.growth.raw").toString.toFloat

        val estDate7 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(7))
        val avg7 = JsonPath.read(trend, "$.epsTrend.7daysAgo.raw").toString.toFloat

        val estDate30 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(30))
        val avg30 = JsonPath.read(trend, "$.epsTrend.30daysAgo.raw").toString.toFloat

        val estDate60 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(60))
        val avg60 = JsonPath.read(trend, "$.epsTrend.60daysAgo.raw").toString.toFloat

        val estDate90 = Timestamp.valueOf(LocalDateTime.now.truncatedTo(ChronoUnit.DAYS).minusDays(90))
        val avg90 = JsonPath.read(trend, "$.epsTrend.90daysAgo.raw").toString.toFloat
        val sdf = new SimpleDateFormat("yyyy-MM-dd")
        val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond(sdf.parse(endDate.get).getTime/1000L,0, ZoneOffset.UTC))
        list=list.appended(StockEarningEstimate(symbol,estDate, period, date  , avg, low, high, no_of_analysis, growth))
        list=list.appended(StockEarningEstimate(symbol,estDate7, period, date  , avg7, low, high, no_of_analysis, growth))
        list=list.appended(StockEarningEstimate(symbol,estDate30, period, date  , avg30, low, high, no_of_analysis, growth))
        list=list.appended(StockEarningEstimate(symbol,estDate60, period, date  , avg60, low, high, no_of_analysis, growth))
        list=list.appended(StockEarningEstimate(symbol,estDate90, period, date  , avg90, low, high, no_of_analysis, growth))
      }
    })
    list
  }
}
