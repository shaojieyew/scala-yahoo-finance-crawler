package finance.scraper.yahoo

import com.jayway.jsonpath.JsonPath
import finance.Util
import finance.model.Stock
import io.circe.{Json, parser}

import scala.util.matching.Regex


object test222 extends App{
  println(StockAnalysis.getStockAnalysis("GOOGL").name)
}

object StockAnalysis {

  def getStockAnalysis(symbol: String): Stock =  {
    val json = crawl(symbol)
    parse(json.getOrElse("").toString)
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

  def parse(json: String): Stock = {
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

    Stock(symbol,
      name,
      industry,
      sector,
      country,
      market,
      exchange,
      website,
      description,
      quote_type,
      exchange_timezone_name,
      is_esg_populated,
      is_tradeable,
      full_time_employees)
  }
}
