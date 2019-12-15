import pandas as pd
from datetime import datetime
import numpy as np
from datetime import timedelta, date

import matplotlib.dates as mdates
from scipy import stats
import datetime as dt

import psycopg2
def open_conn():
    return psycopg2.connect(user = "postgres", password = "admin", host = "127.0.0.1",port = "5432", database = "postgres")

def close_conn(connection):
    if(connection):
        connection.close()

def get_data(stock):
    connection = open_conn()
    data = pd.read_sql_query("SELECT * FROM finance.stock_price where stock = '"+stock+"' and date>'2018-05-01' order by date asc",con=connection)
    close_conn(connection)
    return 

def get_stocks():
    connection = open_conn()
    data = pd.read_sql_query("SELECT a.stock, cap, vol, beta from "+
                            "( "+
                            "SELECT avg(marketcap) as cap, avg(beta) as beta, stock "+
                            "  FROM finance.stock_valuation_stats  where date > '2019-01-01' group by stock "+
                            ") a, " +
                            "( "+
                            "SELECT avg(vol) as vol, stock "+
                            "FROM finance.stock_price where date > '2019-01-01' group by stock "+
                            ") b "+
                            "  WHERE a.stock = b.stock "+
                            "    and cap is not null and beta >1 "+
                            "  order by cap desc, vol desc, beta desc ",con=connection)
    close_conn(connection)
    return data

def main():
    print(get_stocks().head())
    

if __name__== "__main__":
    main()