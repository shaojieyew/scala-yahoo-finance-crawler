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
        self.db = Database()
        #strategies = [StrategyRandom(),StrategyTurtle(),StrategyTrend()]
        #strategies = [StrategyTrend()]
        strategies_MACD = [StrategyMACD()]
        strategies_NNFX = [StrategyNNFX()]

        strgy_batch= [strategies_MACD, strategies_NNFX]
        strgy_reportname= ["strategies_MACD","strategies_NNFX"]
        strgy_test=[0,1]
        stocks  = self.db.get_forex()[0:100]["stock"].values
        #stocks  = get_stocks()[60:100]["stock"].values
        #stocks  = get_sg_stocks()[0:10]["stock"].values

        start = "2000-01-01"
        end = "2019-12-01" 
        for i in range(0,len(strgy_batch)):
            if(strgy_test[i]==1):
                reportname = strgy_reportname[i]
                self.multi_backtest(stocks, 1, strategy=strgy_batch[i], cash = 100000,
                            transaction_fee =0.000, report ="./report/" + reportname+ ".csv", start = start,end = end )
    
    def multi_backtest(self,stocks, total_tries, strategy=[StrategyMACD()], cash = 10000, transaction_fee =0.01, report='report', start = "2017-01-01",end = "2019-12-01"):
        df = pd.DataFrame(columns=['strategy', 'profit', 'start', 'end']) 
        
        for strgy in strategy:
            
            accumulated_growth = 0
            tries = 0
            for i in range(0,total_tries):
                for stock in stocks:
                    data=self.db.get_data(stock,start,end)
                    result = back_test(strgy,data, cash = cash, transaction_fee =transaction_fee, 
                                    target=0.08, risk_percent = 0.01, risk_atr_multiplier =3)
                    profit = result[0]
                    print(stock+" # "+strgy.__class__.__name__)
                    accumulated_growth = accumulated_growth + profit
                        
                    
                    if(result[0]!=0):
                        tries = tries+ 1
            
            print("########## "+str(round(accumulated_growth/tries,2))+"% ######## tries: "+str(tries))
            df = df.append( {
                "strategy": strgy.__class__.__name__,
                "profit": round(accumulated_growth/tries,2),
                "start": start,
                "end": end
            }, ignore_index=True)
        df.to_csv(report, index = None, header=True)
        
Main().run().show()