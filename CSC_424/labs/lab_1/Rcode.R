##GDP
olympics <- read.csv("C:/Users/Main/Desktop/olympics.csv");
d<-olympics;
x=d$ISO.country.code;
y1=d$X2011.GDP;
y2=d$Gold.medals;
y3=d$Silver.medals;
y4=d$Bronze.medals;
plot(as.numeric(x),y2,main="GDP to Medals",col="gold",xlab="",ylab="Medals",pch=19,xaxt="n");
points(as.numeric(x),y3,col="gray",pch=24,xaxt="n");
points(as.numeric(x),y4,col="brown",pch=25,xaxt="n");
par(new=TRUE);
plot(as.numeric(x),y1,type='h',xlab="",ylab="",xaxt="n",yaxt="n");
axis(1,at=x,labels=x);
legend("top",title="Type of Medal", c("Gold","Silver","Bronze"), fill=(c("gold","gray","brown")));


##Population
olympics <- read.csv("C:/Users/Main/Desktop/olympics.csv");
d<-olympics;
x=d$ISO.country.code;
y1=d$X2010.population;
y2=d$Gold.medals;
y3=d$Silver.medals;
y4=d$Bronze.medals;
plot(as.numeric(x),y2,main="Population to Medals",col="gold",xlab="",ylab="Medals",pch=19,xaxt="n");
points(as.numeric(x),y3,col="gray",pch=24,xaxt="n");
points(as.numeric(x),y4,col="brown",pch=25,xaxt="n");
par(new=TRUE);
plot(as.numeric(x),y1,type='h',xlab="",ylab="",xaxt="n",yaxt="n");
axis(1,at=x,labels=x);
legend("top",title="Type of Medal", c("Gold","Silver","Bronze"), fill=(c("gold","gray","brown")));



##GDP Per Capita
olympics <- read.csv("C:/Users/Main/Desktop/olympics.csv");
d<-olympics;
x=d$ISO.country.code;
y1=(d$X2011.GDP/d$X2010.population);
y2=d$Gold.medals;
y3=d$Silver.medals;
y4=d$Bronze.medals;
plot(as.numeric(x),y2,main="GDP Per Capita to Medals",col="gold",xlab="",ylab="Medals",pch=19,xaxt="n");
points(as.numeric(x),y3,col="gray",pch=24,xaxt="n");
points(as.numeric(x),y4,col="brown",pch=25,xaxt="n");
par(new=TRUE);
plot(as.numeric(x),y1,type='h',xlab="",ylab="",xaxt="n",yaxt="n");
axis(1,at=x,labels=x);
legend("top",title="Type of Medal", c("Gold","Silver","Bronze"), fill=(c("gold","gray","brown")));



##Contestants 
olympics <- read.csv("C:/Users/Main/Desktop/olympics.csv");
d<-olympics;
x=d$ISO.country.code;
y1=(d$Female.count+d$Male.count);
y2=d$Gold.medals;
y3=d$Silver.medals;
y4=d$Bronze.medals;
plot(as.numeric(x),y2,main="Number of Contestants to Medals",col="gold",xlab="",ylab="Medals",pch=19,xaxt="n");
points(as.numeric(x),y3,col="gray",pch=24,xaxt="n");
points(as.numeric(x),y4,col="brown",pch=25,xaxt="n");
par(new=TRUE);
plot(as.numeric(x),y1,type='h',xlab="",ylab="",xaxt="n",yaxt="n");
axis(1,at=x,labels=x);
legend("top",title="Type of Medal", c("Gold","Silver","Bronze"), fill=(c("gold","gray","brown")));
