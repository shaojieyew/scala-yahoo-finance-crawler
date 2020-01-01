import pandas as pd
from DatabaseMgr import Database
from strategy.StrategyMACD import *
from strategy.StrategyNNFX import *
from BackTestAlgo import back_test
from Chart import *
class Main:
    def __init__(self):
        super().__init__()

    def run(self):
        db = Database()

        stock="EURHUF=X"
        sdate='2017-01-01'
        edate='2019-12-01'
        strgy=StrategyNNFX()
        data = db.get_data(stock,sdate,edate)

        #processed_df = process_data(data)
        result = back_test(strgy, data, cash = 100000, transaction_fee =0.00, assets={},
                                        target=0.7, risk_percent = 0.01, risk_atr_multiplier =3)
        
        return plot_chart(data, result ,stock , strgy)
    
Main().run().show()