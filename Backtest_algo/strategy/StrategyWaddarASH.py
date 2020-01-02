from strategy.Strategy import Strategy
from TA import process_col, name_col

class StrategyWaddarASH(Strategy):
    def preprocess(self, data):
        
        self.sma_close_1 = 20 
        
        self.ssl_param_1 = 14 
        
        self.waddah_param1 = 150 #sensitive
        self.waddah_param2 = 20 #fast_period
        self.waddah_param3 = 40 #slow_period
        self.waddah_param4 = 20 #channel_period
        self.waddah_param5 = 2 #channel_mult
        self.waddah_param6 = 30 #dead_zone
        
        self.ash_param1 = 9 #timeperiod
        self.ash_param2 = 2 #smooth
        
        self.rvi_param1 = 14 #timeperiod
        self.adx_param1 = 14 #timeperiod
    
        self.sma_close = name_col("sma","close",self.sma_close_1)
        self.ssl_up = name_col("ssl_up",self.ssl_param_1)
        self.ssl_down = name_col("ssl_down",self.ssl_param_1)
    
        self.waddah_bull = name_col("waddah_bull",self.waddah_param1,self.waddah_param2,self.waddah_param3,self.waddah_param4,self.waddah_param5,self.waddah_param6)
        self.waddah_bear = name_col("waddah_bear",self.waddah_param1,self.waddah_param2,self.waddah_param3,self.waddah_param4,self.waddah_param5,self.waddah_param6)
        self.waddah_explo = name_col("waddah_explo",self.waddah_param1,self.waddah_param2,self.waddah_param3,self.waddah_param4,self.waddah_param5,self.waddah_param6)
        self.waddah_dead = name_col("waddah_dead",self.waddah_param1,self.waddah_param2,self.waddah_param3,self.waddah_param4,self.waddah_param5,self.waddah_param6)
        
        self.ASH_bull = name_col("ASH_bull",self.ash_param1,self.ash_param2)
        self.ASH_bear = name_col("ASH_bear",self.ash_param1,self.ash_param2)
        
        self.rvi = name_col("rvi",self.rvi_param1)
        self.rvi_signal = name_col("rvi_signal",self.rvi_param1)
        
        self.adx = name_col("adx",self.adx_param1)
        
        process_col(data,"sma","close",self.sma_close_1)
        process_col(data,"ssl",self.ssl_param_1)
        process_col(data,"waddah",self.waddah_param1,self.waddah_param2,self.waddah_param3,self.waddah_param4,self.waddah_param5,self.waddah_param6)
        process_col(data,"ash",self.ash_param1,self.ash_param2)
        process_col(data,"rvi",self.rvi_param1)
        process_col(data,"adx",self.rvi_param1)
        
    def get_action(self, data, i, shares_can_buy, shares_can_sell, target, risk_percent, risk_atr_multiplier):
        action ="HOLD", 0 , data
        if(
            i>100
            and True
        ):
            data = self.set_stoploss(data, i , 14, risk_atr_multiplier, 
                early_stop_profit=target ,stop_early_times=1.5, danger_stop_atr=6)
            action = "BUY", data.close[i], data
            
            
        if(
            shares_can_sell>0 
            and True
        ):
            action = "SELL", data.close[i], data

        return action
    
    def get_col_to_plot(self):
        plot1 = []
        plot2 = []
        plot3 = []
        plot4 = []
        plot5 = []
        
        plot1.append((self.sma_close,"lines"))
        plot1.append((self.ssl_down,"lines"))
        plot1.append((self.ssl_up,"lines"))
        plot2.append((self.waddah_bull ,"lines"))
        plot2.append((self.waddah_bear ,"lines"))
        plot2.append((self.waddah_explo,"lines"))
        plot2.append((self.waddah_dead ,"lines"))
        plot3.append((self.ASH_bull,"lines"))
        plot3.append((self.ASH_bear,"lines"))
        plot4.append((self.adx,"lines"))
        plot5.append((self.rvi,"lines"))
        plot5.append((self.rvi_signal,"lines"))
        
        return plot1,plot2,plot3,plot4,plot5