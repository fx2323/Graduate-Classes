#load required libraries
library(fBasics)
library(lmtest)
library(forecast)
library(fpp)

#set variable dataSet to the dataset
dataSet<-EURUSD_Candlestick_1_D_BID_01.01.2004.31.12.2013

##transform the data

##remove the data from dataSet when no trading happen so when volume is 0
dataSet=dataSet[dataSet$Volume!=0,]


###############
# Close Model #
###############
adf.test(dataSet$Close)
kpss.test(dataSet$Close)
#just look at the close data
data <- diff(dataSet$Close, lag=1)
adf.test(data)
kpss.test(data)

##data is no longer stationary

#Create Histogram
hist(data,xlab="Close",prob=TRUE, main="Histogram")
#add approximating normal density curve
xfit<-seq(min(data),max(data),length=length(data))
yfit<-dnorm(xfit,mean=mean(data),sd=sd(data))
lines(xfit,yfit, col="blue", lwd=2)

#Create Normal Probability Plot
qqnorm(data)
qqline(data,col=2)

#Normality Tests
normalTest(data,method=c("jb"))

#plot acf and pacf values
acf(data,plot=T)
pacf(data,plot=T)

#Box test
Box.test(data,lag=35,type="Ljung")


###############
# High Model  #
###############
adf.test(dataSet$High)
kpss.test(dataSet$High)
#just look at the High data
data <- diff(dataSet$High, lag=1)
adf.test(data)
kpss.test(data)

#Create Histogram
hist(data,xlab="High",prob=TRUE, main="Histogram")
#add approximating normal density curve
xfit<-seq(min(data),max(data),length=length(data))
yfit<-dnorm(xfit,mean=mean(data),sd=sd(data))
lines(xfit,yfit, col="blue", lwd=2)

#Create Normal Probability Plot
qqnorm(data)
qqline(data,col=2)

#Normality Tests
normalTest(data,method=c("jb"))

#plot acf and pacf values
acf(data,plot=T)
pacf(data,plot=T)

#Box test
Box.test(data,lag=35,type="Ljung")

#fit the model
m1=arima(data, order = c(0,0,1), seasonal = list(order = c(1, 0, 1), period = 6))
m1

#diagnostics
coeftest(m1)
plot(m1$residuals,main="High Model Residuals")
acf(m1$residuals)
qqnorm(m1$residuals)
qqline(m1$residuals)
normalTest(m1$residuals,method=c("jb"))
Box.test(m1$residuals,lag=35, type="Ljung",fitdf=1)

#compute predictions for up to 6 days ahead(1 week in market) also after 6 values repeat
f_High=forecast.Arima(m1,h=6)
f_High
plot(f_High$mean, type="l",ylim=c(min(f_Low$lower[,2]),max(f_Low$upper[,2])))
##had to add then subtract the mean to get indexing right. It looks funny but it works
lines(f_High$mean-f_High$mean+f_High$upper[,2],type="l",col="red")
lines(f_High$mean-f_High$mean+f_High$lower[,2],type="l",col="blue")

###############
# Low Model   #
###############
adf.test(dataSet$Low)
kpss.test(dataSet$Low)
#just look at the Low data
data <- diff(dataSet$Low, lag=1)
adf.test(data)
kpss.test(data)


#Create Histogram
hist(data,xlab="Low",prob=TRUE, main="Histogram")
#add approximating normal density curve
xfit<-seq(min(data),max(data),length=length(data))
yfit<-dnorm(xfit,mean=mean(data),sd=sd(data))
lines(xfit,yfit, col="blue", lwd=2)

#Create Normal Probability Plot
qqnorm(data)
qqline(data,col=2)

#Normality Tests
normalTest(data,method=c("jb"))

#plot acf and pacf values
acf(data,plot=T)
pacf(data,plot=T)

#Box test
Box.test(data,lag=35,type="Ljung")

#fit the model
data=ts(data)
m1=arima(data, order = c(0,0,1), seasonal = list(order = c(1, 0, 1), period = 6))
m1

#diagnostics
coeftest(m1)
plot(m1$residuals,main="Low Models Residuals")
acf(m1$residuals)
qqnorm(m1$residuals)
qqline(m1$residuals)
Box.test(m1$residuals,lag=35, type="Ljung",fitdf=1)
normalTest(m1$residuals,method=c("jb"))

#compute predictions for up to 6 days ahead(1 week in market) also after 6 values repeat
f_Low=forecast.Arima(m1,h=6)
f_Low
plot(f_Low$mean, type="l",ylim=c(min(f_Low$lower[,2]),max(f_Low$upper[,2])))
lines(f_Low$mean-f_Low$mean+f_Low$upper[,2],type="l",col="red")
lines(f_Low$mean-f_Low$mean+f_Low$lower[,2],type="l",col="blue")


#############################
# Forcast Models Together   #
#############################

##add the high and the low prediction models together on same plot
plot(f_Low$mean, type="l",ylim=c(min(f_Low$lower[,2]),max(f_High$upper[,2])),col='blue',main="Both Low and High Predictions",ylab="Change in Price")
lines(f_Low$mean-f_Low$mean+f_Low$upper[,2],type="l",col="blue")
lines(f_Low$mean-f_Low$mean+f_Low$lower[,2],type="l",col="blue")
lines(f_High$mean, type="l",col='red')
lines(f_High$mean-f_High$mean+f_High$upper[,2],type="l",col="red")
lines(f_High$mean-f_High$mean+f_High$lower[,2],type="l",col="red")