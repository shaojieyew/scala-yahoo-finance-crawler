package finance.model

import java.sql.Timestamp

case class StockRating(stock: String,
                       firm: Option[String],
                       from_grade: Option[String],
                       to_grade: Option[String],
                       action: Option[String],
                       graded_timestamp : Timestamp
                       )
