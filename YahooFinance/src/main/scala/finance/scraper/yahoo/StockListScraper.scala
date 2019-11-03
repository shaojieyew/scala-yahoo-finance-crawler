package finance.scraper.yahoo

import finance.Util

import scala.util.matching.Regex

object StockListScraper {
  val STOCKS_SG = "https://sg.finance.yahoo.com/screener/unsaved/9acb0a49-73de-425c-abe3-5a50943d8418"
  val STOCKS_US = "https://sg.finance.yahoo.com/screener/unsaved/a0c769a9-36ee-4e52-948e-49983db91cfc"
  val STOCKS_SG_US = "  https://sg.finance.yahoo.com/screener/unsaved/a06bc07d-799c-4a99-b9bc-6c90c23d1776"
  val STOCKS_UNDERVALUED_LARGE_CAPS = "https://sg.finance.yahoo.com/screener/predefined/undervalued_large_caps"

  @throws(classOf[requests.UnknownHostException])
  @throws(classOf[Exception])
  def get(url: String): List[String] = {
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
