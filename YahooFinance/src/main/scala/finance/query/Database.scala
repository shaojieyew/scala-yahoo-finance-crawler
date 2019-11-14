package finance.query

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import finance.App


trait Database{
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  var xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://%s".format(App.dbaddress),
    App.dbuser, App.dbpassword
  )

  val TIMESTAMP_PATTERN ="yyyy-MM-dd_HH:mm"

}