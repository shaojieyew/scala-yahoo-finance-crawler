from TA import *

def back_test(strategy, data, cash = 100000,transaction_fee = 0.01, assets={}, min_transaction = 1000, target=0.02, risk_percent = 0.01, risk_atr_multiplier = 3):

    longs = {}
    shorts = {}
    init_cash = cash
    bought_at = 100000
    bought_at_list = []
    loss_trade_count = 0
    win_trade_count = 0
    win_percent = 0
    loss_percent = 0
    
    process_col(data,"atr_risk",14)
    strategy.preprocess(data)
    
    for i,row in data.iterrows():
        shares_can_buy = int(cash/(row.close*(1+transaction_fee)))
        shares_can_sell=0
        if row.stock in assets:
            shares_can_sell = assets[row.stock]
        #Decision
        recommended_shares = -1
        shares_held = 0
        if(i>0):
            current_assets = cash
            if(data.iloc[i].stock in assets):
                shares_held = assets[data.iloc[i-1].stock]
                current_assets = shares_held*data.iloc[i-1].close + cash
            risk_atr = data["atr_risk_14"][i-1]
            last_close = data["close"][i-1]
            import math
            if(math.isnan(risk_atr)==False):
                risk_atr =  risk_atr * risk_atr_multiplier
                risk_amt = current_assets * risk_percent
                if(risk_atr!=0):
                    recommended_shares = int(risk_amt/risk_atr)
                    if(shares_can_buy>0):
                        if(shares_held+shares_can_buy>recommended_shares):
                            shares_can_buy = int(recommended_shares-shares_held)            
                else:
                    continue
            else:
                continue
        #if(recommended_shares<=0 or recommended_shares*row.close*(1+transaction_fee)<min_transaction):
        #    continue
        
        action , price_point, data = strategy.get_action(data, i, shares_can_buy, shares_can_sell, target, risk_percent, risk_atr_multiplier)
        #Execution
        if(action=="BUY"):
            if(shares_can_buy>0):
                cost = shares_can_buy*price_point*(1+transaction_fee)
                cash_left = cash-cost
                if row.stock not in assets:
                    assets[row.stock]=0
                assets[row.stock] = assets[row.stock] + shares_can_buy
                cash = cash_left
                longs[row.date] = price_point
                bought_at = price_point
                bought_at_list = bought_at_list + [price_point]
                #print("BUY : +" +str(shares_can_buy) +" @"+str(price_point))
        if(action=="SELL"):
            if(shares_can_sell>0):
                asset = shares_can_sell * price_point
                cash = cash + asset*(1-transaction_fee)
                assets.clear()
                shorts[row.date] = price_point
                #trade counter
                for trade in bought_at_list:
                    if(trade>price_point):
                        loss_trade_count = loss_trade_count +1
                        loss_percent = loss_percent + ((price_point-trade)/trade)
                    else:
                        win_trade_count = win_trade_count +1
                        win_percent = win_percent + ((price_point-trade)/trade)
                bought_at_list = []
                #print("SELL : -" +str(shares_can_sell) +" @"+str(price_point))
        if(action=="STOPPED"):
            if(shares_can_sell>0):
                asset = shares_can_sell * price_point
                cash = cash + asset*(1-transaction_fee)
                assets.clear()
                shorts[row.date] = price_point
                #trade counter
                for trade in bought_at_list:
                    if(trade>price_point):
                        loss_trade_count = loss_trade_count +1
                        loss_percent = loss_percent + ((price_point-trade)/trade)
                    else:
                        win_trade_count = win_trade_count +1
                        win_percent = win_percent + ((price_point-trade)/trade)
                bought_at_list = []
                #print("STOPPED : -" +str(shares_can_sell) +" @"+str(price_point))
    final_cash = cash
    if(data.iloc[-1].stock in assets):
        final_cash = assets[data.iloc[-1].stock]*data.iloc[-1].close + cash
    assets.clear()
    growth =((final_cash-init_cash)/init_cash)*100
    print("profit: "+str(round(growth,2))+"%"+"\t"+"asset: "+str(final_cash))
    print("\t"+"win count: "+str(win_trade_count)+"\t"+"loss count: "+str(loss_trade_count))
    print("\t"+"win percent: "+str(win_percent)+"\t"+"loss percent: "+str(loss_percent))
    return round(growth,2), longs, shorts,  win_trade_count, loss_trade_count, win_percent, loss_percent, final_cash
