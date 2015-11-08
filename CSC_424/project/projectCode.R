##install.packages("arulse");
##install.packages("caret");
library(caret);
library(arules);
set.seed(3456);


fileLocation="C:/Users/Main/Desktop/424/424_project/";
pairs=c("EURJPY","GBPJPY","USDJPY","EURUSD","GBPUSD","EURGBP");
minLength=2;
numberOfGroups=6;
support=.01;
confidence=0.65;



graphs=c("High","Low","Open","Close");


for( i in 1:length(pairs))
{
	##load dataFile and put into variable data
	eval(parse(text =paste("data<-read.csv(\"",fileLocation,pairs[i],"_Candlestick_1_D_BID_01.01.2004-31.12.2013.csv\")",sep="")));

	##make database of each offset candlestick
	eval(parse(text=paste(pairs[i],"_candlesticks<- data.frame(
##Open offset
(data[2:length(data$Open),2]-data[1:(length(data$Close)-1),5])
##High offset
,(data[2:length(data$Open),3]-data[1:(length(data$Close)-1),5])
##Low offset
,(data[2:length(data$Open),4]-data[1:(length(data$Close)-1),5])
##Close offset
,(data[2:length(data$Open),5]-data[1:(length(data$Close)-1),5])

)",sep="")));

##change dataset column names to Open,High,Low,Close
eval(parse(text=paste("names(",pairs[i],"_candlesticks)[1]<-\"Open\"",sep="")));
eval(parse(text=paste("names(",pairs[i],"_candlesticks)[2]<-\"High\"",sep="")));
eval(parse(text=paste("names(",pairs[i],"_candlesticks)[3]<-\"Low\"",sep="")));
eval(parse(text=paste("names(",pairs[i],"_candlesticks)[4]<-\"Close\"",sep="")));

##remove weekend days from data use pair one to determine weekend
if(i==1){
str="";
	for( j in 1:length(graphs)){
		str<-paste(str,pairs[i],"_candlesticks$",graphs[j],"[]==00",sep="");
		if(j<length(graphs)){str<-paste(str," & ",sep="")}
	}
	eval(parse(text=paste("x<-",str,sep="")));
}
eval(parse(text=paste(pairs[i],"_candlesticks<-",pairs[i],"_candlesticks[x==FALSE,]",sep="")));
eval(parse(text=paste("rownames(",pairs[i],"_candlesticks) <- NULL",sep="")));

##use data rows of the offsetted candlestick data with K-means to make numberOfGroups groups
eval(parse(text=paste("Group<-kmeans(",pairs[i],"_candlesticks,numberOfGroups,iter.max=1000)",sep="")));


##add group number to current dataset 
eval(parse(text=paste(pairs[i],"_candlesticks[\"Group\"]<-Group$cluster",sep="")));

##build new dataset that is the data of the k-means group N-2,N-1,N,N+1
eval(parse(text=paste("data","<-data.frame("
,pairs[i],"_candlesticks[1:(length(",pairs[i],"_candlesticks$Open)-3),5],"
,pairs[i],"_candlesticks[2:(length(",pairs[i],"_candlesticks$Open)-2),5],"
,pairs[i],"_candlesticks[3:(length(",pairs[i],"_candlesticks$Open)-1),5],"
,pairs[i],"_candlesticks[4:(length(",pairs[i],"_candlesticks$Open)),5]"
,")",sep="")));


##change dataset column names to N_Minus2, N_Minus1, N ,N_Plus1 

eval(parse(text=paste("names(","data",")[1]<-\"N_Minus2\"",sep="")));
eval(parse(text=paste("names(","data",")[2]<-\"N_Minus1\"",sep="")));
eval(parse(text=paste("names(","data",")[3]<-\"N\"",sep="")));
eval(parse(text=paste("names(","data",")[4]<-\"N_Plus1\"",sep="")));


##make the groups Binany 
Ns=c("N_Minus2","N_Minus1","N","N_Plus1");
for( k in 1:length(Ns)){
	for( j in 1:numberOfGroups){
		string=paste(Ns[k],"_",as.character(j),sep="");
		eval(parse(text=paste(string,"<-data.frame(data$",Ns[k],"==",j,")",sep="")));	

		if(k==1 && j==1){
			eval(parse(text=paste(pairs[i],"<-data.frame(",string,")",sep="")));
			eval(parse(text=paste("names(",pairs[i],")[1]<-\"",pairs[i],"_N_Minus2_1\"",sep="")));
		}
		else{
		eval(parse(text=paste(pairs[i],"[\"",pairs[i],"_",string ,"\"]<-",string,sep="")));
		}
	}
}

##Save the data
eval(parse(text=paste("save(",pairs[i],",file=\"",fileLocation,pairs[i],".Rda\")",sep="")));
eval(parse(text=paste("save(",pairs[i],"_candlesticks, file=\"",fileLocation,pairs[i],"_candlesticks.Rda\")",sep="")));

##eval(parse(text=paste(,sep="")));


}

##build master dataset and save it
string=pairs[1];
for( i in 2:length(pairs))
{
	string<-paste(string,",",pairs[i],sep="");
}
eval(parse(text=paste("masterData<-data.frame(",string,")",sep="")));
save(masterData,file=paste(fileLocation,"mastersData.Rda",sep=""));

##build the test and training dataset
trainIndex<-createDataPartition(masterData$EURUSD_N_Minus2_1, p=.8,list=FALSE, times=1);
head(trainIndex);
train<-masterData[trainIndex,];
test<- masterData[-trainIndex,];

##build the LSH and RSH
LHS<-c()
RHS<-c()
names=c("_N_Minus2_","_N_Minus1_","_N_","_N_Plus1_");
for( k in 1:length(pairs)){
  for( i in 1:length(names)){
    for ( j in 1:numberOfGroups){
      if(i<4){
        LHS<-cbind(LHS,paste(pairs[k],names[i],as.character(j),"=TRUE",sep=""));
      }else{
        RHS<-cbind(RHS,paste(pairs[k],names[i],as.character(j),"=TRUE",sep=""));
      }
    }
  }
}


rules <- apriori(train,parameter = list(minlen=minLength, supp=support, conf=confidence),appearance = list(lhs=LHS, rhs=RHS,default="none"),control = list(verbose=T));

# find redundant rules
rules.sorted <- sort(rules, by="lift");
subset.matrix <- is.subset(rules.sorted, rules.sorted);
subset.matrix[lower.tri(subset.matrix, diag=T)] <- NA;
redundant <- colSums(subset.matrix, na.rm=T) >= 1;

# remove redundant rules
rules.pruned <- rules.sorted[!redundant];
inspect(rules.pruned);













##save the rules
eval(parse(text=paste("save(rules.sorted",",file=\"",fileLocation,"rules",".Rda\")",sep="")));

##build confusion matrix to show end results
confusionMat<- array(0,dim=c(length(pairs)*numberOfGroups,length(pairs)*numberOfGroups));


##runs through each row on the test group of data
for( row in 1:length(test[,1])){
  
  ##build output count double array
  outputCount <- array(0,dim=c(length(pairs),numberOfGroups));
  ##set all values to 0
  
  ##run through all rules
  for( i in 1:length(rules.pruned)){
    
    ##create a string of the condishions needed for the rules as array of strings
    rightside=array(unlist(strsplit(labels(rules.pruned[i]),"=>")))[1];
    rightside=rightside[1];
    rightside=substr(rightside,2,nchar(rightside)-2);
    rightside=array(unlist(strsplit(rightside,",")));
    for( j in 1:length(rightside)){
      rightside[j]=sub("=TRUE","",rightside[j]);
    }
    for( j in 1:length(rightside)){
      if(eval(parse(text=paste("test$",rightside[j],"[",row,"]","==TRUE",sep="")))==FALSE){
        break;
      }
      ##all condishion rules were met add to count
      if(j==length(rightside)){
        ##see condishion rules output as string
        leftside=array(unlist(strsplit(labels(rules.pruned[i]),"=>")))[2];
        leftside=leftside[1];
        leftside=substr(leftside,3,nchar(leftside)-6);
        group=as.integer(substr(leftside,nchar(leftside),nchar(leftside)));
        for( k in 1:length(pairs)){
          if(grepl(substr(leftside,1,6),pairs[k])){
            outputPair=k;	
          }
        }
        ##adds to total count
        outputCount[outputPair,group]= outputCount[outputPair,group] +1;
      }
      
    }
  }##ends for loop on all rules
  
  ##add into confusion matrix
  for( i in 1:length(pairs)){
    ##if max is greating than 0 prediction is made
    if(max(outputCount[i,])>0){
      
      for( j in 1:numberOfGroups){
        ##look for max index on prediction
        if(outputCount[i,j]==max(outputCount[i,])){
          predicted_value=(((i-1)*numberOfGroups)+j);		
        }
        ##look for actural group
        if(eval(parse(text=paste("test$",pairs[i],"_N_Plus1_",as.character(j),"[row]==TRUE",sep="")))){
          actual_value=(((i-1)*numberOfGroups)+j);
        }
      }
      ##update confusion matrix
      confusionMat[actual_value,predicted_value]=confusionMat[actual_value,predicted_value]+1;	
    }
  }##ends confusion matrix update
  print(paste("Test case ",row, " of ", length(test[,1]),sep=""));
  
}##ends testing each row in test data group




##build a outputSuccess and performance net plot and plot each candlestick data of predicted group
pdf(paste(fileLocation,"output.pdf",sep=""));
outputSuccess <- array(0,dim=c(length(pairs),numberOfGroups));
outputPerformance <- array(0,dim=c(length(pairs),numberOfGroups));
for( i in 1:length(pairs)){
  for( j in 1:numberOfGroups){
    if(sum(confusionMat[,(((i-1)*numberOfGroups)+j)])>0){
      
      outputPerformance[i,j]<-((confusionMat[(((i-1)*numberOfGroups)+j),(((i-1)*numberOfGroups)+j)]/sum(confusionMat[,(((i-1)*numberOfGroups)+j)]))-eval(parse(text=paste("sum(masterData$",pairs[i],"_N_Plus1_",as.character(j),")/length(masterData$",pairs[i],"_N_Plus1_",as.character(j),")",sep=""))));	
      
      outputSuccess[i,j]<-(confusionMat[(((i-1)*numberOfGroups)+j),(((i-1)*numberOfGroups)+j)]/sum(confusionMat[,(((i-1)*numberOfGroups)+j)]));	
      
      ##build a normal curve of open,high,close,low
      str=paste(pairs[i],"_candlesticks$Group[]==",j,sep="");
      eval(parse(text=paste("x<-",str,sep="")));
      eval(parse(text=paste("x<-",pairs[i],"_candlesticks[x,]",sep="")));
      
      par(mfrow=c(2,2))
      for( k in 1:length(graphs)){
        eval(parse(text=paste("plotData=x$",graphs[k],sep="")));
        plot(plotData,main=paste(pairs[i]," Group ",j," ",graphs[k],sep=""),type="p",xlab="",ylab="Price");
      }
    }
  }
}
dev.off();
sink(paste(fileLocation,"output.txt",sep=""), append=FALSE, split=FALSE);
print(outputPerformance);
print(outputSuccess);
sink();

