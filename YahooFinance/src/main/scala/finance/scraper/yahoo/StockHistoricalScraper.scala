package finance.scraper.yahoo

import java.net.HttpCookie
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneOffset}
import java.util.{Date, TimeZone}

import finance.Util
import finance.model.{StockDividend, StockPrice}

import scala.scalajs.niocharset.StandardCharsets
import scala.util.matching.Regex

object StockHistoricalTest extends App{
  var yahooHistorical = new StockHistoricalScraper("G3B.SI")
  //println(yahooHistorical.getDividends())
  //yahooHistorical.getHistorical()
  yahooHistorical.getDividends()
  //println(yahooHistorical.getSplits())
}

class StockHistoricalScraper(symbol: String, start: Array[Int]=Array(1976,1,1), end: Array[Int]=Array(), interval: String ="1d"){
  val api_url = "https://query1.finance.yahoo.com/v7/finance/download/%s?period1=%s&period2=%s&interval=%s&events=%s&crumb=%s"
  symbol.toUpperCase
  val (crumb, cookies)=getCrumb(symbol)
  val sdf = new SimpleDateFormat("yyyy-MM-dd")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
  val start_dt = sdf.parse(start(0)+"-"+start(1)+"-"+start(2))
  val start_epoch = start_dt.getTime/1000L


  val currentTime = LocalDateTime.now
  val end_dt = {
    if(end.nonEmpty){
      sdf.parse(end(0)+"-"+end(1)+"-"+end(2))
    }  else  {
      sdf.parse(currentTime.getYear()+"-"+currentTime.getMonthValue+"-"+currentTime.getDayOfMonth)
    }
  }
  val end_epoch = end_dt.getTime/1000L

  def getCrumb(ticker: String): (String, Map[String, HttpCookie]) ={
    val url  = "https://finance.yahoo.com/quote/%s/history".format(ticker)
    val r = requests.get(url)
    Util.printLog("StockHistorical url=%s".format(r.url))
    val pattern = new Regex("\\\"CrumbStore\\\":\\{\\\"crumb\\\":\\\".*\\\"},\\\"StreamStore\\\"")
    var crumb = (pattern findFirstIn r.text).getOrElse("")
      .replace("\\u002F","")
      .replace("\"CrumbStore\":{\"crumb\":\"","")
      .replace("\"},\"StreamStore\"","")
    (crumb, r.cookies)
  }

  def getData(events: String): String ={
    if (Array("1d","1wk","1mo").filter( _.toUpperCase() == interval.toUpperCase()).length==0)
      throw new Exception("Incorrect interval: valid intervals are 1d, 1wk, 1mo")
    val url = api_url.format(symbol,start_epoch,end_epoch,interval,events,crumb)
    println(new HttpCookie("B",cookies.getOrElse("B","").toString.drop(2)))
    val data = requests.get(url,cookies = Map("B"->new HttpCookie("B",cookies.getOrElse("B","").toString.drop(2))))
    new String(data.contents, StandardCharsets.UTF_8)
  }

  def getHistoricalString(): String ={
    """Returns a list of historical price data from Yahoo Finance"""
    getData("history")
  }

  def getDividendsString(): String ={
    """Returns a list of historical dividends data from Yahoo Finance"""
    getData("div")
  }

  def getSplitsString(): String ={
    """Returns a list of historical splits data from Yahoo Finance"""
    getData("split")
  }

  def getDividends(): List[StockDividend] ={
    Util.printLog("StockHistorical: scrapping StockDividend")
    if (Array("1d","1wk","1mo").filter( _.toUpperCase() == interval.toUpperCase()).length==0)
      throw new Exception("Incorrect interval: valid intervals are 1d, 1wk, 1mo")
    val url = api_url.format(symbol,start_epoch,end_epoch,interval,"div",crumb)
    Util.printLog(url)
    val data = requests.get(url,cookies = Map("B"->new HttpCookie("B",cookies.getOrElse("B","").toString.drop(2))))
    var lineCount =0
    var list = List[StockDividend] ()
    var cols = List[String]()
    val result =  new String(data.contents, StandardCharsets.UTF_8)
    result.split("\\R+").foreach(line=>{
      lineCount = lineCount +1
      if (lineCount==1){
        cols = line.split(",").toList
      }else{
        try {
          val values = line.split(",")
          val dividend = values(cols.indexOf("Dividends")).toFloat
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val dividend_date = values(cols.indexOf("Date"))
          val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond(sdf.parse(dividend_date).getTime / 1000L, 0, ZoneOffset.of("+08:00")))
          list = list.appended(StockDividend(symbol, dividend, date))

        }catch{
          case e: Exception => {}
        }
      }
    })
    list
  }

  def getHistorical(): List[StockPrice] ={
    Util.printLog("StockHistorical: scrapping StockPrice")
    if (Array("1d","1wk","1mo").filter( _.toUpperCase() == interval.toUpperCase()).length==0)
      throw new Exception("Incorrect interval: valid intervals are 1d, 1wk, 1mo")
    val url = api_url.format(symbol,start_epoch,end_epoch,interval,"history",crumb)
    Util.printLog(url)
    val data = requests.get(url,cookies = Map("B"->new HttpCookie("B",cookies.getOrElse("B","").toString.drop(2))))
    var lineCount =0
    var list = List[StockPrice] ()
    var cols = List[String]()
    val result =  new String(data.contents, StandardCharsets.UTF_8)
    result.split("\\R+").foreach(line=>{
      lineCount = lineCount +1
      if (lineCount==1){
        cols = line.split(",").toList
      }else{
        try {
          val values = line.split(",")
          val open = values(cols.indexOf("Open")).toFloat
          val high = values(cols.indexOf("High")).toFloat
          val low = values(cols.indexOf("Low")).toFloat
          val close = values(cols.indexOf("Close")).toFloat
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val price_date = values(cols.indexOf("Date"))
          val date = Timestamp.valueOf(LocalDateTime.ofEpochSecond(sdf.parse(price_date).getTime / 1000L, 0, ZoneOffset.of("+08:00")))
          val adj_close = values(cols.indexOf("Adj Close")).toFloat
          val vol = values(cols.indexOf("Volume")).toLong
          list = list.appended(StockPrice(symbol, open, high, low, close, adj_close, vol, date))
        }catch{
          case e: Exception => {}
        }
      }
    })
    list
  }
}
