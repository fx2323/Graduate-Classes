# -*- coding: utf-8 -*-
"""
Final 521 Andrew Tillmann

This script file is located here:
C:\Users\Main\.spyder2\final521_AndrewTillmann.py
"""
__author__='Andrew Tillmann'
import sys;
import pickle;
from nlib import YStock, random,exp;
##import matplotlib.pyplot as plt;
fileName=str(sys.argv[1]);
Ndays=int(sys.argv[2]);
Percent=float(int(sys.argv[3])/100.0);
data = pickle.load(open(fileName));
numberOfDays=250;
stockDict=dict();
errorStockDict=dict();


##new way
def get_data(stockString):
    if(stockString not in stockDict):
        stock=YStock(stockString).historical();
        stockDict[stockString]=stock[-numberOfDays:];
        if(len([day['log_return'] for day in stockDict[stockString]])!=numberOfDays):
            errorStockDict[stockString]=stockDict[stockString];
    if(stockString in errorStockDict):
        return False;
    return True;

def simulate_position(nshares,stockString,days):
    P0=stockDict[stockString][-1]['close'];
    R = sum(stockDict[stockString][-day]['log_return'] for day in days)
    Pt =  P0* exp(R) ##last days close times expected return
    PandL = (Pt-P0)*nshares ##overall profit and loss
    return PandL

def simulate_client(data,client,days):
        clientSum=0;
        ##loop overall all stock held by client
        for stock in data[client]:
            if(get_data(stock)==True): ##gets and checks data of stock returns            
                clientSum +=simulate_position(data[client][stock],stock,days);
                ##return clientSum; ##uncomment this and simulation will only run on first stock
        return clientSum;
        
def VAR_client(data,client,N=1000):
    results = [];
    for k in range(N):
        days=[random.choice(range(1,numberOfDays)) for k in range(Ndays)];
        PL = simulate_client(data,client,days);
        results.append(PL);
    ##plt.plot(results);
    results.sort();
    results.reverse();
    return -results[int(Percent*N)]; ##need to flip the sign since that is how VAR is reported

def VAR_brokerageHouse(data,N=1000):
    results = [];
    for k in range(N):
        days=[random.choice(range(1,numberOfDays)) for k in range(Ndays)];
        PL = [simulate_client(data,client,days) for client in data];
        results.append(sum(PL));
    results.sort();
    results.reverse();
    return -results[int(Percent*N)]; ##need to flip the sign since that is how VAR is reported
        
##lines of code to start file simulation frist clients then brokreage
##loop through all clients
for client in data:
    print client,VAR_client(data,client);

##print out brokerage House VAR
print 'Brokerage House ',VAR_brokerageHouse(data);