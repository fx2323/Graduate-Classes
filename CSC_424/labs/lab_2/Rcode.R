


string="atemp + hum + windspeed"

for ( i in 1:3){
	string=paste(string," + S",as.character(i),sep="");
	eval(parse(text=paste("BikeShareDay$S",as.character(i),"<-(BikeShareDay$season==",as.character(i),")*1", sep="")));
}

for ( i in 1:3){
	string=paste(string," + W",as.character(i),sep="");
	eval(parse(text=paste("BikeShareDay$W",as.character(i),"<-(BikeShareDay$weathersit==",as.character(i),")*1", sep="")));
}

for ( i in 0:5){
	string=paste(string," + WD",as.character(i),sep="");
	eval(parse(text=paste("BikeShareDay$WD",as.character(i),"<-(BikeShareDay$weekday==",as.character(i),")*1", sep="")));
}

for ( i in 1:11){
	string=paste(string," + Mnth",as.character(i),sep="");
	eval(parse(text=paste("BikeShareDay$Mnth",as.character(i),"<-(BikeShareDay$mnth==",as.character(i),")*1", sep="")));
}

eval(
	parse(
		text=paste("RegisteredModel<-lm(registered~",string,",data=BikeShareDay)",sep="")
	)
);

plot(pred[,1],pred[,2],ylab="PCA1",xlab="PCA2", main="PCA1 vs. PCA2")
text(x=pred[,1], y=pred[,2], labels=RainForest$Name)

plot(pred[,1],pred[,3],ylab="PCA1",xlab="PCA3", main="PCA1 vs. PCA3")
text(x=pred[,1], y=pred[,3], labels=RainForest$Name)


plot(pred[,2],pred[,3],ylab="PCA2",xlab="PCA3", main="PCA2 vs. PCA3")
text(x=pred[,2], y=pred[,3], labels=RainForest$Name)
