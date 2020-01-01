def plot_chart(stock_prices, result, stock, strategy):
    ######Plot
    ######
    growth, longs, shorts,  win_trade_count, loss_trade_count, win_percent, loss_percent, final_cash=result
    import plotly.graph_objs as go 
    from datetime import datetime
    from ipywidgets import interact, interactive, fixed, interact_manual
    import ipywidgets as widgets
    from plotly.subplots import make_subplots
    # Make sure dates are in ascending order
    # We need this for slicing in the callback below
    data = stock_prices
    df = data.set_index('date')
    fig = go.FigureWidget(make_subplots(rows=4, cols=1))
    
    
    
    fig.add_trace(
        go.Scattergl(x=list(df.index), y=list(df['close']),
        name = "close"),
        row=1, col=1
    )
    if('stoploss' in df.columns):
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df['stoploss']), line = dict(color='firebrick', width=2, dash='dash'),
            name = "stoploss"),
            row=1, col=1
        )
    if('stoploss_fix' in df.columns):
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df['stoploss_fix']), line = dict(color='firebrick', width=2, dash='dash'),
            name = "stoploss_fix"),
            row=1, col=1
        )
        
    if('stoploss_danger' in df.columns):
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df['stoploss_danger']), line = dict(color='firebrick', width=2, dash='dash'),
            name = "stoploss_danger"),
            row=1, col=1
        )
    fig.add_trace(
        go.Scattergl(x=[i for i in shorts], y=[shorts[i] for i in shorts],
        name = "Short", mode="markers", marker=dict(color="red", size=5)),
        row=1, col=1
    )
    fig.add_trace(
        go.Scattergl(x=[i for i in longs], y=[longs[i] for i in longs],
        name = "Long", mode="markers", marker=dict(color="green", size=5)),
        row=1, col=1
    )
    plot1,plot2,plot3,plot4 = strategy.get_col_to_plot()
    
    for p in plot1:
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df[p]),
            name = p),
            row=1, col=1
        )
    for p in plot2:
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df[p]),
            name = p),
            row=2, col=1
        )
    for p in plot3:
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df[p]),
            name = p),
            row=3, col=1
        )
    for p in plot4:
        fig.add_trace(
            go.Scattergl(x=list(df.index), y=list(df[p]),
            name = p),
            row=4, col=1
        )
    fig.layout.xaxis=dict(
            anchor='x',
            rangeselector=dict(
                buttons=list([
                    dict(count=1,
                         label='1m',
                         step='month',
                         stepmode='backward'),
                    dict(count=6,
                         label='6m',
                         step='month',
                         stepmode='backward'),
                    dict(count=1,
                        label='YTD',
                        step='year',
                        stepmode='todate'),
                    dict(count=1,
                        label='1y',
                        step='year',
                        stepmode='backward'),
                    dict(step='all')
                ])
            ),
            type='date'
        )
    fig.layout.yaxis=dict(
            domain=[0.53, 1]
        )
    fig.layout.yaxis2=dict(
            domain=[0.35, 0.5]
        )
    fig.layout.yaxis3=dict(
            domain=[0.16, 0.34]
        )
    fig.layout.yaxis4=dict(
            domain=[0.0, 0.15]
        )
    #fig.layout.yaxis2.range=[0,100]
    #fig.layout.yaxis2.tickvals=[0,20,80,100]
    fig['layout'].update(height=700, width=1000) 
    
    import time
    import datetime
    def zoom(layout, xrange):
        try:
            fig.layout.xaxis2.range = fig.layout.xaxis.range
            fig.layout.xaxis3.range = fig.layout.xaxis.range
            fig.layout.xaxis4.range = fig.layout.xaxis.range
            in_view = df.loc[fig.layout.xaxis.range[0]:fig.layout.xaxis.range[1]]
            padding = 0.1
            
            from_price = in_view.iloc[0].close
            to_price = in_view.iloc[-1].close
            growth = (to_price-from_price)/from_price*100.0
            
            max_val_y = in_view["close"].max()
            min_val_y =  in_view["close"].min()
            axis_diff = max_val_y-min_val_y
            fig.layout.yaxis.range = [min_val_y - (axis_diff*padding), max_val_y + (axis_diff*padding)]
            
            if(len(plot2)>0):
                max_val_y = in_view[plot2[0]].max()
                min_val_y =  in_view[plot2[0]].min()
                axis_diff = max_val_y-min_val_y
                fig.layout.yaxis2.range = [min_val_y - (axis_diff*padding), max_val_y + (axis_diff*padding)]
         
            if(len(plot3)>0):
                max_val_y = in_view[plot3[0]].max()
                min_val_y =  in_view[plot3[0]].min()
                axis_diff = max_val_y-min_val_y
                fig.layout.yaxis3.range = [min_val_y - (axis_diff*padding), max_val_y + (axis_diff*padding)]
         
            if(len(plot4)>0):
                max_val_y = in_view[plot4[0]].max()
                min_val_y =  in_view[plot4[0]].min()
                axis_diff = max_val_y-min_val_y
                fig.layout.yaxis4.range = [min_val_y - (axis_diff*padding), max_val_y + (axis_diff*padding)]
         
            fig['layout'].update(title=stock+" ("+str(round(growth,2))+"%)") 
        except Exception as e:
            None
    fig.layout.on_change(zoom, 'xaxis')
    return fig