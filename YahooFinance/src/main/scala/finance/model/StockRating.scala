package finance.model

import java.sql.Timestamp

case class StockRating(stock: String,
                       firm: String,
                       from_grade: String,
                       to_grade: String,
                       action: String,
                       graded_timestamp : Timestamp
                       )
