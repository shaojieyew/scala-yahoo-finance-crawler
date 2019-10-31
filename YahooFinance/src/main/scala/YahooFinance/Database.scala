package YahooFinance
import cats.effect._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts


object testDB extends App{
  case class Stock(symbol: String)

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def find(n: String): ConnectionIO[Option[Stock]] =
    sql"select symbol from finance.stock where symbol = $n".query[Stock].option

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432",
    "postgres", "admin"
  )
  println(find("GOOGL").transact(xa).unsafeRunSync)

}

class Database {

}
