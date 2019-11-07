package finance

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Util {
  def printLog(msg: String) = println(msg)

  val DATE_FORMAT = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.S")

  def NOW_TIMESTAMP = Timestamp.valueOf(LocalDateTime.now)
}
