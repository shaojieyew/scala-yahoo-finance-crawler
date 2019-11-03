package finance.query

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts


trait Database{
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432",
    "postgres", "admin"
  )

  val TIMESTAMP_PATTERN ="yyyy-MM-dd_HH:mm"

}