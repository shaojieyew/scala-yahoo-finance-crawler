package finance.scraper.yahoo

import java.net.HttpCookie
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import finance.Util

import scala.scalajs.niocharset.StandardCharsets
import scala.util.matching.Regex

object StockHistoricalTest extends App{
  var yahooHistorical = new StockHistorical("G3B.SI",Array(2018,10,29))
  println(yahooHistorical.getDividends())
  println(yahooHistorical.getHistorical())
  println(yahooHistorical.getSplits())
}

class StockHistorical(symbol: String, start: Array[Int], end: Array[Int]=Array(), interval: String ="1d"){
  val api_url = "https://query1.finance.yahoo.com/v7/finance/download/%s?period1=%s&period2=%s&interval=%s&events=%s&crumb=%s"
  symbol.toUpperCase
  val (crumb, cookies)=getCrumb(symbol)
  val sdf = new SimpleDateFormat("yyyy-MM-dd")
  sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
  val start_dt = sdf.parse(start(0)+"-"+start(1)+"-"+start(2))
  val start_epoch = (start_dt.getTime/1000).toInt
  val currentTime = new Date()
  val end_dt = {
    if(end.nonEmpty){
      sdf.parse(end(0)+"-"+end(1)+"-"+end(2))
    }  else  {
      sdf.parse(currentTime.getYear()+"-"+currentTime.getMonth+"-"+currentTime.getDate)
    }
  }
  val end_epoch = (end_dt.getTime/1000).toInt

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
    println(url)
    println(new HttpCookie("B",cookies.getOrElse("B","").toString.drop(2)))
    val data = requests.get(url,cookies = Map("B"->new HttpCookie("B",cookies.getOrElse("B","").toString.drop(2))))
    new String(data.contents, StandardCharsets.UTF_8)
  }

  def getHistorical(): String ={
    """Returns a list of historical price data from Yahoo Finance"""
    getData("history")
  }

  def getDividends(): String ={
    """Returns a list of historical dividends data from Yahoo Finance"""
    getData("div")
  }

  def getSplits(): String ={
    """Returns a list of historical splits data from Yahoo Finance"""
    getData("split")
  }
}
