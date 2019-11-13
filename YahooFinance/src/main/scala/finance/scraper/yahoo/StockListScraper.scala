package finance.scraper.yahoo

import finance.Util

import scala.io.Source
import scala.util.matching.Regex

object StockListScraper {



  def main(args: Array[String]): Unit = {
    //println (getYahooFinance("https://sg.finance.yahoo.com/screener/350511a6-ba9b-40cd-8814-1e1c807ad371").foreach(println(_)))
    println(getCsv("Data/Listing/SGX.csv"))
  }
  def getCsv(url: String): List[String] ={
    val bufferedSource = Source.fromFile(url)
    val symbols = for (line <- bufferedSource.getLines) yield "%s.SI".format(line.split(",")(1))
    val list = symbols.toList.drop(1)
    bufferedSource.close
    list
  }

  def get(url: String): List[String] = {
    getCsv(url)
  }
  @throws(classOf[requests.UnknownHostException])
  @throws(classOf[Exception])
  def getYahooFinance(url: String): List[String] = {
    var res: List[String] = List()
    var count = 0
    val pattern = new Regex("<a href=\\\"/quote/[a-zA-Z-\\.]*\\?p=[a-zA-Z\\-\\.]*\\\" title=\\\"")
    while (true) {
      var stocks: List[String] = List()
      val r = requests.get(url + "?offset=" + (count * 100) + "&count=100")
      Util.printLog("getListOfStocks url=%s".format(r.url))
      stocks = (pattern findAllIn r.text).map(_
        .replaceAll("<a href=\"/quote/[a-zA-Z-\\.]*\\?p=", "")
        .replaceAll("\" title=\"", "")
      ).toList
      res = res ::: stocks
      if (stocks.isEmpty)
        return res
      count = count + 1
    }
    res
  }

}
