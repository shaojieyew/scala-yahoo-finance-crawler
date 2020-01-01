from strategy.Strategy import Strategy
from TA import process_col, name_col

class StrategyMACD(Strategy):
    def __init__(self):
        super().__init__()
        
    def preprocess(self, data):
        self.macd_param_1 = 12
        self.macd_param_2 = 26
        self.macd_param_3 = 9
    
        self.macd = name_col("macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.macd_signal = name_col("macd_signal",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.macd_hist = name_col("macd_hist",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        
        process_col(data,"macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        
    def get_action(self, data, i, shares_can_buy, shares_can_sell, target, risk_percent, risk_atr_multiplier):
        action ="HOLD", 0 , data
        
        
        if(
            data[self.macd][i]>data[self.macd_signal][i]
            and data[self.macd][i-1]<data[self.macd_signal][i-1]
        ):
            data = self.set_stoploss(data, i , 14, risk_atr_multiplier, 
                early_stop_profit=target ,stop_early_times=1.5, danger_stop_atr=6)
            action = "BUY", data.close[i], data
            
            
        if(
            data[self.macd][i]<data[self.macd_signal][i]
            and data[self.macd][i-1]>data[self.macd_signal][i-1]
        ):
            action = "SELL", data.close[i], data

        '''
        if(shares_can_sell>0):
            if(i>0 
               and data["stoploss"][i]>data.low[i]
              ):
                action = "STOPPED", data["stoploss"][i], data
        
        '''
        return action
    
    def get_col_to_plot(self):
        plot1 = []
        plot2 = []
        plot3 = []
        
        plot2.append(self.macd)
        plot2.append(self.macd_signal)
        plot2.append(self.macd_hist)
        
        return plot1,plot2,plot3

class StrategyMACD1(StrategyMACD):
    def __init__(self):
        self.macd_param_1 = 20
        self.macd_param_2 = 50
        self.macd_param_3 = 5
        