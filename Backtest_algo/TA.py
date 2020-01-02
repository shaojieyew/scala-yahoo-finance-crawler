import numpy as np

def process_col(data, col="", *argv):
    params = '_'.join(str(x) for x in argv)
    if(col+"_"+params in data.columns):
        return
    
    if(col=="zero"):
        data['zero'] = np.full(len(data), 0)
       
    if(col=="atr_risk"):
        from talib import ATR
        data["atr_risk_"+params]= ATR(data['high'].values, data['low'].values, data['close'].values ,timeperiod=argv[0])
    
    if(col=="macd"):
        from talib import MACD
        data['macd_'+params], data['macd_signal_'+params], data['macd_hist_'+params] = MACD(data['close'], fastperiod=argv[0], slowperiod=argv[1], signalperiod=argv[2])

    if(col=="rsi"):
        from talib import RSI
        data['rsi_'+params] = RSI(data['close'].values, timeperiod=argv[0])

    if(col=="adx"):
        from talib import ADX
        data['adx_'+params] =  ADX(data['high'].values, data['low'].values, data['close'].values, timeperiod=argv[0])

    if(col=="kijunsen"):
        data['kijunsen_'+params] = KIJUNSEN(data['high'],data['low'], timeperiod=argv[0])
        
    if(col=="ema"):
        from talib import EMA
        data['ema_'+params] = EMA(data[argv[0]], timeperiod=argv[1])
        
    if(col=="sma"):
        from talib import SMA
        data['sma_'+params] = SMA(data[argv[0]], timeperiod=argv[1])
        
    if(col=="hma"):
        data['hma_'+params] = HMA(data[argv[0]], timeperiod=argv[1])
        
    if(col=="linearreg"):
        from talib import LINEARREG_ANGLE
        data['linearreg_'+params] = LINEARREG_ANGLE(data[argv[0]], timeperiod=argv[1])
        
    if(col=="linearreg"):
        from talib import LINEARREG_ANGLE
        data['linearreg_'+params] = LINEARREG_ANGLE(data[argv[0]], timeperiod=argv[1])
    
    if(col=="atr_stoploss"):
        from talib import LINEARREG_ANGLE
        data['atr_stoploss_'+params] = ATR_STOPLOSS(close = data.close.values, 
                                                   high = data.high.values, 
                                                   low = data.low.values, 
                                 times=argv[0], stop_early_times=argv[1], early_stop_profit=argv[2], period=argv[3], repaint=True)[0]

    if(col=="atr"):
        from talib import ATR
        data['atr_'+params] = ATR(data['high'].values, data['low'].values, data['close'].values ,timeperiod=argv[0])
    
    
    if(col=="ssl"):
        data["ssl_up_"+params],data["ssl_down_"+params]= SSL(data['high'].values, data['low'].values, data['close'].values ,timeperiod=argv[0])
    
    
    if(col=="ha"):
        data["ha_open"],data["ha_high"],data["ha_low"],data["ha_close"] = HEIKIN_ASHI(data['open'].values, data['high'].values, data['low'].values, data['close'].values)

    if(col=="rvi"):
        data["rvi_"+params],data["rvi_signal_"+params]= RVI(data['high'].values, data['low'].values, data['close'].values, data['open'].values,timeperiod=argv[0])

    if(col=="waddah"):
        data["waddah_bull_"+params],data["waddah_bear_"+params],data["waddah_explo_"+params],data["waddah_dead_"+params]= WADDAH_ATTAR_EXPLOSION(data['close'].values,data['high'].values,data['low'].values, sensitive = argv[0] , fast_period= argv[1], slow_period =  argv[2], channel_period =  argv[3], channel_mult =  argv[4], dead_zone= argv[5])
    
    if(col=="ash"):
        data["ASH_bull_"+params],data["ASH_bear_"+params]= ASH(data['close'].values, timerperiod=argv[0], smooth =argv[1])


def name_col(col="", *argv):
    params = '_'.join(str(x) for x in argv)
    return col+"_"+params

def SSL(high,low, close, timeperiod=7):
    from talib import EMA
    sma_high = EMA(high,timeperiod )
    sma_low = EMA(low,timeperiod )
    
    ssl_up = np.full(len(close), np.nan)
    ssl_down = np.full(len(close), np.nan)
    lv = np.full(len(close), np.nan)
    lv[timeperiod-0]=1
    for i in range(0,len(close)):
        if(i>=timeperiod):
            if(close[i]<sma_low[i]):
                lv[i] = -1
            else:
                if(close[i]>sma_high[i]):
                    lv[i] = 1
                else:
                    lv[i] = 0
            if(lv[i]==0):
                lv[i] = lv[i-1]
                
            if(lv[i]>0):
                ssl_up[i] = sma_high[i]
                ssl_down[i] = sma_low[i]
            else:
                ssl_up[i] = sma_low[i]
                ssl_down[i] = sma_high[i]
    return ssl_up, ssl_down
def RVI(high, low, close, open, timeperiod = 7):
    rvi = np.full(len(close), np.nan)
    signal = np.full(len(close), np.nan)
    numerator = np.full(len(close), np.nan)
    denominator = np.full(len(close), np.nan)
    for i in range(0,len(close)):
        if(i>=3):
            a = close[i] - open[i]
            b = close[i-1] - open[i-1] 
            c = close[i-2] - open[i-2] 
            d = close[i-3] - open[i-3] 
            e = high[i] - low[i]
            f = high[i-1]  - low[i-1] 
            g = high[i-2]  - low[i-2] 
            h = high[i-3]  - low[i-3]
            numerator[i]=(a+(2*b)+(2*c)+d)/6
            denominator[i]=(e+(2*f)+(2*g)+h)/6
        if(i>=(3+timeperiod)):
            sma_numerator = np.array(numerator)[i-(timeperiod-1):i+1].mean()
            sma_denominator = np.array(denominator)[i-(timeperiod-1):i+1].mean()
            rvi[i] = sma_numerator/sma_denominator
        
        if(i>=6+timeperiod):
            signal[i] = (rvi[i] + (2*rvi[i-1]) + (2*rvi[i-2]) +rvi[i-3])/6
    return rvi,signal

def VWAP(vol, high, low, close):
    cum_vol = vol.cumsum()
    cum_vol_price = (vol * (high + low + close ) /3).cumsum()
    return cum_vol_price / cum_vol

def MATR(high,low, close ,timeperiod=14):
    import statistics
    tr = high - low
    matr = np.full(len(close)+1, np.nan)
    for i in range(0,len(matr)):
        if(i>=timeperiod):
            matr[i]=statistics.median(np.array(tr[i-(timeperiod-1):i+1]))
    return matr

def CLOSE_ATR(high,low, close ,timeperiod=14):
    from talib import SMA
    sma_high = SMA(high,timeperiod )
    sma_low = SMA(low,timeperiod )
    import statistics
    tr = sma_high - sma_low
    matr = np.full(len(close)+1, np.nan)
    for i in range(0,len(matr)):
        if(i>=timeperiod):
            matr[i]=(np.array(tr[i-(timeperiod-1):i+1])).max()
    return matr

def ATR_STOPLOSS(close, high, low, times=3, period=14, early_stop_profit=0.02, stop_early_times=3, repaint = False, median=False, average_high_low=False):
    starting_close = close[period]
    from talib import ATR
    if(average_high_low == False):
        if(median==False):
            atr= ATR(high,low, close ,timeperiod=period)
        else:
            atr=MATR(high,low, close ,timeperiod=period)
    else:
        atr=CLOSE_ATR(high,low, close ,timeperiod=period)
    first = 1
    import math
    atr_trailing_stoploss = np.full(len(close)+1, np.nan)
    for i in range(0,len(close)):
        if(i>0 and math.isnan(atr[i])==False and repaint==True):
            if((close[i-1]<atr_trailing_stoploss[i-1] and close[i]>atr_trailing_stoploss[i])
              or (close[i-1]>atr_trailing_stoploss[i-1] and close[i]<atr_trailing_stoploss[i])):
                starting_close = close[i]
        multiplier = times
        if(i>0 and math.isnan(atr[i])==False):
            if(close[i-1]>atr_trailing_stoploss[i] and close[i]<atr_trailing_stoploss[i]):
                loss = atr[i]*multiplier
                atr_trailing_stoploss[i+1]=close[i]+loss  
                continue
        if(i>0 and math.isnan(atr[i])==False):
            if(close[i-1]<atr_trailing_stoploss[i] and close[i]>atr_trailing_stoploss[i]):
                loss = atr[i]*multiplier
                atr_trailing_stoploss[i+1]=close[i]-loss  
                continue
        if(math.isnan(atr[i])==False):
            loss = atr[i]*multiplier
            atr_trailing_stoploss[i+1]=close[i]-loss
            if(close[i]<atr_trailing_stoploss[i]):
                if(atr_trailing_stoploss[i]>close[i]+loss):
                    '''
                    ratio_to_target = ((close[i]-starting_close)/(starting_close))/early_stop_profit
                    if(((close[i]-starting_close)/(starting_close))<-early_stop_profit):
                        loss = atr[i]*stop_early_times
                    else:
                        if(ratio_to_target<0):
                            loss = atr[i]*(times-((times - stop_early_times)*(-ratio_to_target)))
                        else:
                            loss = atr[i]*multiplier
                    '''
                    atr_trailing_stoploss[i+1]=close[i]+loss
                else:
                    if(math.isnan(atr_trailing_stoploss[i])==False):
                        atr_trailing_stoploss[i+1]=atr_trailing_stoploss[i]
            else:
                if(atr_trailing_stoploss[i]<close[i]-loss):
                    ratio_to_target = ((close[i]-starting_close)/(starting_close))/early_stop_profit
                    if(((close[i]-starting_close)/(starting_close))>early_stop_profit):
                        loss = atr[i]*stop_early_times
                    else:
                        if(ratio_to_target>0):
                            loss = atr[i]*(times-((times - stop_early_times)*ratio_to_target))
                        else:
                            loss = atr[i]*multiplier
                    
                    atr_trailing_stoploss[i+1]=close[i]-loss
                else:
                    if(math.isnan(atr_trailing_stoploss[i])==False):
                        atr_trailing_stoploss[i+1]=atr_trailing_stoploss[i]
    return atr_trailing_stoploss[:-1], atr_trailing_stoploss[-1:]

def Normalise(listss):
    min_i = min(listss)
    max_i = max(listss)
    if(min_i==max_i):
        return listss
    return [(x-min_i)/(max_i-min_i) for x in listss]
def SLOPE(data):
    #data = np.array(Normalise(data))
    #data = data * (len(data)-1)
    from talib import LINEARREG_ANGLE
    return list(LINEARREG_ANGLE(data,timeperiod = len(data)-1))[-1]
def PRICE_SLOPE(data, period = 14):
    df = data.tail(period)
    first = df.index.start
    last = df.index.stop
    x = df.index.values.reshape(-1, 1) 
    y= df.close.values.reshape(-1, 1) 
    from sklearn.linear_model import LinearRegression
    reg = LinearRegression().fit(x,y)
    first_predicted = reg.predict(np.array([[first]]))[0][0]
    last_predicted = reg.predict(np.array([[last-1]]))[0][0]
    gradient =(last_predicted-first_predicted)/(last-first)
    return gradient


def HEIKIN_ASHI(open_price, high_price, low_price, close_price):
    ha_close = (open_price+ high_price+ low_price+ close_price) / 4
    ha_open = np.full(len(open_price), np.nan)
    ha_high = np.full(len(open_price), np.nan)
    ha_low = np.full(len(open_price), np.nan)
    for i in range(len(open_price)):
        if i == 0:
            ha_open[0]= open_price[0]
        else:
            ha_open[i] = (ha_open[i-1] + ha_close[i-1]) / 2
        ha_high[i] = np.array([high_price[i], ha_open[i], ha_close[i]]).max()
        ha_low[i] = np.array([low_price[i], ha_open[i], ha_close[i]]).min()
    return ha_open, ha_high, ha_low, ha_close

    '''
    if(i>60):
            last_60days_trend_slope = PRICE_SLOPE(data[0:i], period = 60)
            last_60days_max_high = data[0:i].tail(60).high.max()
            last_60days_min_low = data[0:i].tail(60).low.min()
    '''

    '''
        if(i>30):
            last_30days_trend_slope = PRICE_SLOPE(data[0:i], period = 30)
            last_30days_max_high = data[0:i].tail(30).high.max()
            last_30days_min_low = data[0:i].tail(30).low.min()
    '''

    '''
        if(i>14):
            last_14days_trend_slope = PRICE_SLOPE(data[0:i], period = 14)
            last_14days_max_high = data[0:i].tail(14).high.max()
            last_14days_min_low = data[0:i].tail(14).low.min()
            price_perct_14days = (data["close"][i]-last_14days_min_low)/(last_14days_max_high-last_14days_min_low)
    '''

    '''
        max_rsi = data["rsi"][0:i].max()
        min_rsi = data["rsi"][0:i].min()
        rsi_threshold = (max_rsi-min_rsi)*0.02
    '''

def LINEAR_REGRESSION_BOUND(data_close, data_high, data_low, period = 14, skip = 0):
    linreg_upper = np.full(len(data_close), np.nan)
    linreg_lower = np.full(len(data_close), np.nan)
    for i in range(len(data_close)):
        if(i>(period-1+skip)):
            close = np.array(data_close[i-(period)-skip:i-skip])
            high = np.array(data_high[i-(period)-skip:i-skip])
            low = np.array(data_low[i-(period)-skip:i-skip])
            first = 0
            last = period
            x = np.array(list(range(first,last))).reshape(-1, 1) 
            y= np.array(close).reshape(-1, 1) 
            from sklearn.linear_model import LinearRegression
            reg = LinearRegression().fit(x,y)

            max_high1 = 0
            max_high2 = 0
            max_low1 = 0
            max_low2 = 0
            for index in range(len(close)):
                predicted_price = reg.predict(np.array([[index]]))[0][0]
                high_diff = high[index] - predicted_price
                if(max_high1<high_diff):
                    max_high2 = max_high1
                    max_high1 = high_diff
                else:
                    if(max_high2<high_diff):
                        max_high2 = high_diff

                low_diff =  predicted_price - low[index]
                if(max_low1<low_diff):
                    max_low2 = max_low1
                    max_low1 = low_diff
                else:
                    if(max_low2<low_diff):
                        max_low2 = low_diff
            predicted_close = reg.predict(np.array([[len(close)+skip]]))[0][0]
            linreg_upper[i] = predicted_close+((max_high1+max_high2)/2.0)
            linreg_lower[i] = predicted_close-((max_low1+max_low2)/2.0 )
    return linreg_upper, linreg_lower

def PAST_LOW_HIGH(data, period = 14):
    slop_angle = np.full(len(data), np.nan)
    past_high = np.full(len(data), np.nan)
    past_low = np.full(len(data), np.nan)
    for i in range(len(data)):
        if(i>=period):
            slop_angle[i] = SLOPE(data[i-period:i].close)
            past_high[i] = data[0:i-1].tail(period).high.max()
            past_low[i] = data[0:i-1].tail(period).low.min()
    return slop_angle, past_high, past_low
    
    
def STOCHARSI(rsi, timeperiod=14):
    stocha_rsi = np.full(len(rsi), np.nan)
    for i in range(len(rsi)):
        if(i>=14):
            curr_rsi = rsi[i]
            min_rsi = rsi[i-14:i].min()
            max_rsi = rsi[i-14:i].max()
            stocha_rsi[i]=((curr_rsi-min_rsi)/(max_rsi-min_rsi))*100
            if(stocha_rsi[i]>100):
                stocha_rsi[i]=100
            if(stocha_rsi[i]<0):
                stocha_rsi[i]=0
    return stocha_rsi

def KIJUNSEN(high,low, timeperiod=26):
    kijunsen = np.full(len(high), np.nan)
    for i in range(len(high)):
        if(i>=timeperiod):
            kijunsen[i]=(high[i-timeperiod:i].max()+low[i-timeperiod:i].min())/2
    return kijunsen
    

def HMA(close, timeperiod=14):
    import math
    from talib import WMA
    sqrt_period = math.sqrt(timeperiod)
    wma1 = (2*WMA(close, timeperiod = int(timeperiod/2)))-WMA(close, timeperiod = timeperiod)
    return WMA(wma1,timeperiod =int(sqrt_period))
    
def WADDAH_ATTAR_EXPLOSION(close, high, low, sensitive = 150, fast_period=20, slow_period = 40, channel_period = 20, channel_mult = 2, dead_zone=30):
    
    from talib import MACD 
    from talib import BBANDS 
    from talib import ATR 
    from talib import WMA 
    macd, macdsignal, macdhist = MACD(close, fastperiod=fast_period, slowperiod=slow_period, signalperiod=9)
    upperband, middleband, lowerband = BBANDS(close, timeperiod=channel_period, nbdevup=channel_mult, nbdevdn=channel_mult, matype=0)

    ind_trend1 = np.full(len(close), np.nan)
    ind_itrend1 = np.full(len(close), np.nan)
    ind_explo1 = np.full(len(close), np.nan)
    tr = WMA(ATR(high, low, close, 20),3)
    ind_dead = tr*dead_zone / 10
    for i in range(0,len(close)):
        if(i<2):
            continue
        trend1 = (macd[i] - macd[i-1]) * sensitive;
        trend2 = (macd[i-1] - macd[i-2]) * sensitive;
        explo1 = (upperband[i] - lowerband[i])
        #explo2 = (upperband[i-1] - lowerband[i-1])
        
        if(trend1>=0):
            ind_trend1[i]=trend1
            ind_itrend1[i]=0
        if(trend1<0):
            ind_trend1[i]=0
            ind_itrend1[i]=(trend1*-1)
        ind_explo1[i] = explo1
        #print(str(i)+"\t "+str(close[i])+"\t "+str(close[i])+"\t "+str(ind_trend1[i])+"\t"+str(ind_itrend1[i]))
    return ind_trend1, ind_itrend1, ind_explo1, ind_dead


def ASH(close, timerperiod = 9, smooth = 2):
    from talib import WMA 
    bull = np.full(len(close), np.nan)
    bear = np.full(len(close), np.nan)
    bull[1:] = 0.5*(abs(close[1:]-close[:-1])+(close[1:]-close[:-1]))
    bear[1:] = 0.5*(abs(close[1:]-close[:-1])-(close[1:]-close[:-1]))
    
    avgBull = WMA(bull, timerperiod)
    avgBear = WMA(bear, timerperiod)
    
    smoothBull = WMA(avgBull, smooth)
    smoothBear = WMA(avgBear, smooth)
    
    return smoothBull, smoothBear