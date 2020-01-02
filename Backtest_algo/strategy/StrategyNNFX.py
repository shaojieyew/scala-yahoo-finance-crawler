from strategy.Strategy import Strategy
from TA import process_col, name_col
class StrategyNNFX(Strategy):
    
    def preprocess(self, data):
        self.kijunsen_param_1 = 12
        self.atr_param_1 = 14
        self.macd_param_1 = 12
        self.macd_param_2 = 26
        self.macd_param_3 = 9
        self.sma_kijunsen_1 = 12
        self.sma_kijunsen_2 = 26
        self.atr_stoploss_1_param_1 = 2
        self.atr_stoploss_1_param_2 = 2
        self.atr_stoploss_1_param_3 = 0.02
        self.atr_stoploss_1_param_4 = 14
        self.confirmation_delay = 7

        self.baseline = name_col("kijunsen",self.kijunsen_param_1)
        self.atr = name_col("atr",self.atr_param_1)
        self.c1_co1 = name_col("macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.c1_co2 = name_col("macd_signal",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.c2_co1 = name_col("sma",self.baseline,self.sma_kijunsen_1)
        self.c2_co2 = name_col("sma",self.baseline,self.sma_kijunsen_2)
        self.v1_co1 = "close"
        self.v1_co2 = name_col("atr_stoploss",self.atr_stoploss_1_param_1,
                              self.atr_stoploss_1_param_2,self.atr_stoploss_1_param_3,
                              self.atr_stoploss_1_param_4)
        
        process_col(data,"kijunsen",self.kijunsen_param_1)
        process_col(data,"atr",self.atr_param_1)
        process_col(data,"macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        process_col(data,"sma",self.baseline,self.sma_kijunsen_1)
        process_col(data,"sma",self.baseline,self.sma_kijunsen_2)
        process_col(data,"atr_stoploss",self.atr_stoploss_1_param_1,
                              self.atr_stoploss_1_param_2,self.atr_stoploss_1_param_3,
                              self.atr_stoploss_1_param_4)
    
    def get_action(self, data, i, shares_can_buy, shares_can_sell, target, risk_percent, risk_atr_multiplier):
        import random
        action ="HOLD", 0, data
        
        if(i<100):
            return action
        ########################################################
        #Base Line 1
        baseline=self.baseline
        atr = self.atr
        period = self.confirmation_delay
        baseline_signal=[-1]*period
        baseline_agree=[False]*period
        baseline_atr=[False]*period
        
        for p in range(0,period):
            if(data["open"][i-p]<data[baseline][i-p]
              and data["close"][i-p]>data[baseline][i-p]):
                baseline_signal[p] = 0
                
            if(data["close"][i-p]>data[baseline][i-p]):
                baseline_agree[p] = True
                
            if(data["close"][i-p]<data[baseline][i-p]+data[atr][i-p]):
                baseline_atr[p] = True
        ########################################################
        #confirmation_signal_1
        c1_co1 = self.c1_co1
        c1_co2 = self.c1_co2
        c1_signal=[-1]*period
        c1_agree=[False]*period
        
        for p in range(0,period):
            if(data[c1_co1][i-p]>data[c1_co2][i-p]):
                c1_agree[p] = True
                for d in range(0,period):
                    if(data[c1_co1][(i-p)-d]>data[c1_co2][(i-p)-d]
                      and data[c1_co1][(i-p)-(d+1)]<data[c1_co2][(i-p)-(d+1)]):
                        c1_signal[p] = d
                        break
               
        ########################################################
        #confirmation_signal_2
        c2_co1 = self.c2_co1
        c2_co2 = self.c2_co2
        c2_signal=[-1]*period
        c2_agree=[False]*period
        
        for p in range(0,period):
            if(data[c2_co1][i-p]>data[c2_co2][i-p]):
                c2_agree[p] = True
                for d in range(0,period):
                    if(data[c2_co1][(i-p)-d]>data[c2_co2][(i-p)-d]
                      and data[c2_co1][(i-p)-(d+1)]<data[c2_co2][(i-p)-(d+1)]):
                        c2_signal[p] = d
                        break
                        
        ########################################################
        #volumn_signal
        v1_co1 =self.v1_co1
        v1_co2 =self.v1_co2
        v1_signal=[-1]*period
        v1_agree=[False]*period
        
        for p in range(0,period):
            if(data[v1_co1][i-p]>data[v1_co2][i-p]):
                v1_agree[p] = True
                for d in range(0,period):
                    if(data[v1_co1][(i-p)-d]>data[v1_co2][(i-p)-d]
                      and data[v1_co1][(i-p)-(d+1)]<data[v1_co2][(i-p)-(d+1)]):
                        v1_signal[p] = d
                        break
            
        ########################################################
        #Entry Strategies 
        standard_entry =0
        if(c1_signal[0] == 0
           and baseline_agree[0]
           and baseline_atr[0]
           and c2_agree[0]
           and v1_agree[0]
          ):
            standard_entry = 1
            
        baseline_cross_entry = 0
        if(baseline_signal[0] == 0
          and c1_agree[0]
          and baseline_atr[0]
          and c2_agree[0]
          and v1_agree[0]
          and c1_signal[0] < 7 and c1_signal[0] > -1
          ):
            baseline_cross_entry = 1
            
        pull_back_entry = 0
        if(baseline_signal[1] == 0
          and c1_agree[1]
          and not(baseline_atr[1])
          and baseline_atr[0]
          and c1_agree[0]
          and c2_agree[0]
          and v1_agree[0]
          ):
            pull_back_entry = 1
            
        if(shares_can_buy*data["close"][i]>0 and shares_can_sell==0):
            if(i>50 and
                (
                    standard_entry == 1
                    or
                    baseline_cross_entry == 1
                    or
                    pull_back_entry == 1
                )
            ):
                if(shares_can_sell==0):
                    #setup ATR trailing stoploss 
                    data = self.set_stoploss(data, i , 14, risk_atr_multiplier, 
                                             early_stop_profit=target ,stop_early_times=1.5, danger_stop_atr=6)
                action="BUY", data["close"][i], data            
                
                
        ########################################################
        #exit_signal
        #confirmation_signal_2
        exit_co1 = self.baseline
        exit_co2 = "close"
        exit_signal=[-1]*period
        exit_agree=[False]*period
        
        for p in range(0,period):
            if(data[exit_co1][i-p]>data[exit_co2][i-p]):
                exit_agree[p] = True
                for d in range(0,period):
                    if(data[exit_co1][(i-p)-d]>data[exit_co2][(i-p)-d]
                      and data[exit_co1][(i-p)-(d+1)]<data[exit_co2][(i-p)-(d+1)]):
                        exit_signal[p] = d
                        break
            
        if(shares_can_sell>0):
            if(i>20
                and shares_can_sell > 0
                and exit_signal[0]==0
                and c1_agree[0]==False
                and c2_agree[0]==False
            ):
                action = "SELL", data["close"][i], data
        
        if(shares_can_sell>0):  
            if(i>20
                and data["stoploss_danger"][i]>min(data.low[i],data.close[i])
                and shares_can_sell > 0
            ):  
                action = "STOPPED", data["stoploss_danger"][i], data
            
            
        return action
    
    def get_col_to_plot(self):
        plot1 = []
        plot2 = []
        plot3 = []
        plot4 = []
        plot5 = []
        
        plot1.append((self.baseline,"lines"))
        plot2.append((self.c1_co1,"lines"))
        plot2.append((self.c1_co2,"lines"))
        plot3.append((self.c2_co1,"lines"))
        plot3.append((self.c2_co2,"lines"))
        plot4.append((self.v1_co1,"lines"))
        plot4.append((self.v1_co2,"lines"))
        
        return plot1,plot2,plot3,plot4,plot5
    
class StrategyNNFX1(StrategyNNFX):
    
    def preprocess(self, data):
        self.kijunsen_param_1 = 12
        self.atr_param_1 = 14
        self.macd_param_1 = 12
        self.macd_param_2 = 26
        self.macd_param_3 = 9
        self.atr_stoploss_1_param_1 = 2
        self.atr_stoploss_1_param_2 = 2
        self.atr_stoploss_1_param_3 = 0.02
        self.atr_stoploss_1_param_4 = 14
        self.confirmation_delay = 7
        self.ssl_1_param_1=7

        self.baseline = name_col("kijunsen",self.kijunsen_param_1)
        self.atr = name_col("atr",self.atr_param_1)
        self.c2_co1 = name_col("macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.c2_co2 = name_col("macd_signal",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.c1_co1 = name_col("ssl_up",self.ssl_1_param_1)
        self.c1_co2 = name_col("ssl_down",self.ssl_1_param_1)
        self.v1_co1 = "close"
        self.v1_co2 = name_col("atr_stoploss",self.atr_stoploss_1_param_1,
                              self.atr_stoploss_1_param_2,self.atr_stoploss_1_param_3,
                              self.atr_stoploss_1_param_4)
        
        process_col(data,"kijunsen",self.kijunsen_param_1)
        process_col(data,"atr",self.atr_param_1)
        process_col(data,"macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        process_col(data,"ssl",self.ssl_1_param_1)
        process_col(data,"atr_stoploss",self.atr_stoploss_1_param_1,
                              self.atr_stoploss_1_param_2,self.atr_stoploss_1_param_3,
                              self.atr_stoploss_1_param_4)
    
    
class StrategyNNFX2(StrategyNNFX):
    
    def preprocess(self, data):
        self.kijunsen_param_1 = 12
        self.atr_param_1 = 14
        self.ssl_1_param_1=7
        self.ema_param_1 = 7
        self.ema_param_2 = 50
        self.adx_1_param_1 = 7
        self.ema_adx_param_1 = 7
        self.ema_adx_param_2 = 50
        self.confirmation_delay = 12

        self.baseline = name_col("kijunsen",self.kijunsen_param_1)
        self.atr = name_col("atr",self.atr_param_1)
        self.c1_co1 = name_col("ssl_up",self.ssl_1_param_1)
        self.c1_co2 = name_col("ssl_down",self.ssl_1_param_1)
        self.c2_co1 = name_col("ema","close",self.ema_param_1)
        self.c2_co2 = name_col("ema","close",self.ema_param_2)
        self.adx = name_col("adx",self.adx_1_param_1)
        self.v1_co1 = name_col("ema",self.adx,self.ema_adx_param_1)
        self.v1_co2 = name_col("ema",self.adx,self.ema_adx_param_2)
        
        process_col(data,"kijunsen",self.kijunsen_param_1)
        process_col(data,"atr",self.atr_param_1)
        process_col(data,"ssl",self.ssl_1_param_1)
        process_col(data,"ema","close",self.ema_param_1)
        process_col(data,"ema","close",self.ema_param_2)
        process_col(data,"adx",self.adx_1_param_1)
        process_col(data,"ema",self.adx,self.ema_adx_param_1)
        process_col(data,"ema",self.adx,self.ema_adx_param_2)
    
        
class StrategyNNFX3(StrategyNNFX):
    
    def preprocess(self, data):
        self.kijunsen_param_1 = 12
        self.atr_param_1 = 14
        self.macd_param_1 = 7
        self.macd_param_2 = 20
        self.macd_param_3 = 7
        self.adx_1_param_1 = 14
        self.sma_adx_1_param_1 = 7
        self.confirmation_delay = 7
        self.ssl_1_param_1=7

        self.baseline = name_col("kijunsen",self.kijunsen_param_1)
        self.atr = name_col("atr",self.atr_param_1)
        self.c1_co1 = name_col("ssl_up",self.ssl_1_param_1)
        self.c1_co2 = name_col("ssl_down",self.ssl_1_param_1)
        self.c2_co1 = name_col("macd",self.macd_param_1,self.macd_param_2,self.macc2_co1d_param_3)
        self.c2_co2 = name_col("macd_signal",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        self.v1_co1 = name_col("adx",self.adx_1_param_1)
        self.v1_co2 = name_col("sma",self.v1_co1,self.sma_adx_1_param_1)
        
        process_col(data,"kijunsen",self.kijunsen_param_1)
        process_col(data,"atr",self.atr_param_1)
        process_col(data,"macd",self.macd_param_1,self.macd_param_2,self.macd_param_3)
        process_col(data,"ssl",self.ssl_1_param_1)
        process_col(data,"adx",self.adx_1_param_1)
        process_col(data,"sma",self.v1_co1,self.sma_adx_1_param_1)
    
    