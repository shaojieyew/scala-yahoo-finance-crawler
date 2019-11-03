package finance.model

import java.sql.Timestamp

case class StockSeeder(symbol: String, src: String
                       , created_timestamp: Timestamp
                       , updated_timestamp: Timestamp)
