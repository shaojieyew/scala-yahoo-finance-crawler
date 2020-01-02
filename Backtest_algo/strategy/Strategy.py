
from TA import process_col, name_col, ATR_STOPLOSS
import numpy as np

class Strategy:
    def __init__(self):
        super().__init__()

    def get_action(self, data, i, shares_can_buy, shares_can_sell, cash, target, risk_percent, risk_atr_multiplier):
        pass
    
    def set_stoploss(self, data, i, atr_period = 14, atr_multiplier = 3, early_stop_profit = 0.02, stop_early_times=1, danger_stop_atr=5):
        data.loc[i+1:,("stoploss")] = ATR_STOPLOSS(close = np.array(data["close"][i-atr_period:]), 
                                                         high = np.array(data["high"][i-atr_period:]),
                                                         low = np.array(data["low"][i-atr_period:]), 
                                     times=atr_multiplier, period=atr_period, early_stop_profit=early_stop_profit,stop_early_times=stop_early_times)[0][atr_period+1:]
        data.loc[i+1:,("stoploss_fix")] = ATR_STOPLOSS(close = np.array(data["close"][i-atr_period:]), 
                                                         high = np.array(data["high"][i-atr_period:]),
                                                         low = np.array(data["low"][i-atr_period:]), 
                                     times=atr_multiplier, period=atr_period, early_stop_profit=early_stop_profit,stop_early_times=atr_multiplier)[0][atr_period+1:]
        data.loc[i+1:,("stoploss_danger")] = ATR_STOPLOSS(close = np.array(data["close"][i-atr_period:]), 
                                                         high = np.array(data["high"][i-atr_period:]),
                                                         low = np.array(data["low"][i-atr_period:]), 
                                     times=danger_stop_atr, period=atr_period, early_stop_profit=early_stop_profit,stop_early_times=danger_stop_atr)[0][atr_period+1:]
        
        return data

    def get_col_to_plot(self):
        pass

    def preprocess(self, data):
        pass