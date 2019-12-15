class TA_functions:
    def ATR_STOPLOSS(close, high, low, atr, times=3, period=14):
        first = 1
        import math
        atr_trailing_stoploss = np.full(len(close)+1, np.nan)
        for i in range(0,len(close)):
            
            if(i>0 and math.isnan(atr[i])==False):
                if(close[i-1]>atr_trailing_stoploss[i] and close[i]<atr_trailing_stoploss[i]):
                    loss = atr[i]*times
                    atr_trailing_stoploss[i+1]=close[i]+loss  
                    continue
            if(i>0 and math.isnan(atr[i])==False):
                if(close[i-1]<atr_trailing_stoploss[i] and close[i]>atr_trailing_stoploss[i]):
                    loss = atr[i]*times
                    atr_trailing_stoploss[i+1]=close[i]-loss  
                    continue
                    
            if(math.isnan(atr[i])==False):
                loss = atr[i]*times
                atr_trailing_stoploss[i+1]=close[i]-loss
                if(close[i]<atr_trailing_stoploss[i]):
                    if(atr_trailing_stoploss[i]>close[i]+loss):
                        atr_trailing_stoploss[i+1]=close[i]+loss
                    else:
                        if(math.isnan(atr_trailing_stoploss[i])==False):
                            atr_trailing_stoploss[i+1]=atr_trailing_stoploss[i]
                else:
                    if(atr_trailing_stoploss[i]<close[i]-loss):
                        atr_trailing_stoploss[i+1]=close[i]-loss
                    else:
                        if(math.isnan(atr_trailing_stoploss[i])==False):
                            atr_trailing_stoploss[i+1]=atr_trailing_stoploss[i]
        return atr_trailing_stoploss[:-1], atr_trailing_stoploss[-1:]

    def SLOPE(data):
        first = 0
        last = len(data)
        x = np.array(list(range(first,last))).reshape(-1, 1) 
        y= np.array(data).reshape(-1, 1) 
        from sklearn.linear_model import LinearRegression
        reg = LinearRegression().fit(x,y)
        first_predicted = reg.predict(np.array([[first]]))[0][0]
        last_predicted = reg.predict(np.array([[last]]))[0][0]
        gradient =(last_predicted-first_predicted)/(last-first)
        return gradient

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
