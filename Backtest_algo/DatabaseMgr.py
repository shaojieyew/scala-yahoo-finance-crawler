import psycopg2
import pandas as pd
class Database:
    def __init__(self):
        print("Database init")        
        self.connection = psycopg2.connect(
            user = "postgres", 
            password = "admin", 
            host = "127.0.0.1",
            port = "5432", 
            database = "postgres")

    def get_data(self, stock,date='2018-01-01',date2='2019-01-01'):
        print("Database get_data {}, {}, {}".format(stock,str(date),str(date2)))   
        data = pd.read_sql_query(
            "SELECT * FROM finance.stock_price where stock = '"+stock+"' and date between '"+date+"' and '"+date2+"' order by date asc",
            con=self.connection)
        return data

    def get_stocks(self):
        print("Database get_stocks")   
        data = pd.read_sql_query(
            "SELECT a.stock, cap, vol, beta from "+
            "( "+
            "SELECT avg(marketcap) as cap, avg(beta) as beta, stock "+
            "  FROM finance.stock_valuation_stats  where date > '2017-01-01' group by stock "+
            ") a, " +
            "( "+
            "SELECT avg(vol) as vol, stock "+
            "FROM finance.stock_price where date > '2017-01-01' group by stock "+
            ") b "+
            "  WHERE a.stock = b.stock "+
            "    and cap is not null and beta >0.8 "+
            "  order by cap desc, vol desc, beta desc ",
            con=self.connection)
        return data

    def get_forex(self):
        print("Database get_forex")   
        data = pd.read_sql_query("SELECT symbol as stock FROM finance.stock where quote_type='CURRENCY' and symbol <>'ETHUSD=X' and symbol <> 'BTCUSD=X'" ,
        con=self.connection)
        return data

    def get_sg_stocks(self):
        print("Database get_sg_stocks")   
        data = pd.read_sql_query("SELECT symbol as stock FROM finance.stock where country = 'Singapore'",
        con=self.connection)
        return data
    

    def close_conn(self):
        print("DatabaseConnection close_conn")
        if(self.connection):
            self.connection.close()