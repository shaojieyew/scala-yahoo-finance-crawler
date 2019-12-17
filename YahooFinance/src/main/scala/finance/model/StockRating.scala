package finance.model

import java.sql.Timestamp

case class StockRating(stock: String,
                       firm: Option[String],
                       from_grade: Option[String],
                       to_grade: Option[String],
                       action: Option[String],
                       graded_timestamp : Timestamp
                       )

/*
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Top Pick','0');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Strong Buy','0');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Add','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Buy','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('BUy','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Positive','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Overweight','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Outperform','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Outperformer','1');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Sector Performer','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Sector Perform','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Market Perform','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Long-term Buy','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Accumulate','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Sector Outperform','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Long-Term Buy','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Perform','2');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Average','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Hold Neutral','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Equal-Weight','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Market Weight','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Neutral','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Cautious','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Fair Value','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Equal-weight','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Mixed','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Peer Perform','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Hold','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('In-Line','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Peer perform','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Sector Weight','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('In-line','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Conviction Buy','3');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Below Average','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Reduce','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Sell','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Underperform','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Underperformer','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Market Outperform','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Sector Underperform','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Negative','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Trading Sell','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Market Underperform','4');
INSERT INTO finance.analyst_recommendation_rank (grade,rank) VALUES ('Underweight','4');


 */