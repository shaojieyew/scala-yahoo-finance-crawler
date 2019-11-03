package finance.model

import java.sql.Timestamp

case class Stock(symbol: String,
                 name: String,
                 industry: String,
                 sector: String,
                 country: String,
                 market: String,
                 exchange: String,
                 website: String,
                 description: String,
                 quote_type: String,
                 exchange_timezone_name: String,
                 is_esg_populated: Boolean,
                 is_tradeable :Boolean,
                 full_time_employees: Int)
