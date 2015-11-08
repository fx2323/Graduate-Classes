
#include <RInside.h>                    // for the embedded R via RInside
#include <iostream>
#include <fstream>
#include <limits>

using namespace std;
using namespace Rcpp;

struct Explanatory{
	string name;
	bool inUse;
	
};
struct InfoPass{
	double f_value;
	double adj_R_squared;
	double sigma;
	double best_f;
	double best_r;
	double best_sigma;
	double total_test;
	int size;
	int cycleCount;
	string response;
	string data;
	string fileName;

};

void makeOutput(const char* str){
	ofstream output;
	ifstream temp;
	output.open( "final_output.txt", ios::out | ios::app );
	output<<str;
	output<<"\n";
	temp.open(str);
	string s="";
	getline(temp,s);
	output<<s;
	output<<"\n\n";
	temp.close();
	remove(str);
	output.close();

}	
void toFile(string model,const char* fileName){
	ofstream myfile;
	myfile.open(fileName);
	myfile<<model;
	myfile.close();
}

void findBest(int n,Explanatory inputs[],RInside *R,InfoPass *info){

	for(int i=0;i<2;i++){
		if(i==0){inputs[n].inUse=FALSE;}
		else{inputs[n].inUse=TRUE;}
		if(n>0){findBest(n-1,inputs,R, info);}
		
		//will only get to code below if n==0 thus all input[i].inUse have correct true/false value
		else{
			cout<<"Number of models tested: "<<(*info).cycleCount<<"/"<<(*info).total_test<<"\r";
			//find total number of inputs inUSe
			int count=0;
			for(int j=0;j<(*info).size;j++){if(inputs[j].inUse==TRUE){count++;}}
			if(count==0){continue;} //need to add code for the one case where all inUse=false

			//all inputs.names have either a true(will use) or false(will not use)
			//now build string and send model to R
			string model="model=lm(formula= "+(*info).response+" ~ ";
			for(int j=0;j<(*info).size;j++){
				if(inputs[j].inUse==TRUE){
					model.append(inputs[j].name);
					if(count!=1){model.append(" + ");}
					count--;
				}
			}
			model.append(", data="+(*info).data+");");
			(*R).parseEval(model);
		
			//gets f-value and sets it to highest if it is highest value
			string str="summary(model)$fstatistic[1];";
			Rcpp::StringVector f_value=(*R).parseEval(str);
			double f=atof(f_value[0]);
			if(f>((*info).f_value)){((*info).f_value)=f; toFile(model,"best_f_value");}

			//gets adj.r.squared and sets it to highest if it is the highest value
			str="summary(model)$adj.r.squared;";
			Rcpp::StringVector adj_R_squared=(*R).parseEval(str);
			double adj_R=atof(adj_R_squared[0]);
			if(adj_R>((*info).adj_R_squared)){((*info).adj_R_squared)=adj_R; toFile(model,"best_adj_R_squared");}
		
			//gets sigma and sets it to lowest if it is the lowest value
			str="summary(model)$sigma;";
			Rcpp::StringVector sigma_v=(*R).parseEval(str);
			double sigma=atof(sigma_v[0]);
			if(sigma<((*info).sigma)){((*info).sigma)=sigma; toFile(model,"best_sigma");}
		


			//find the best seleceted if best adj_R and best_f or sigma
			if(adj_R==(*info).best_r && (f==(*info).best_f || sigma==(*info).best_sigma)){
				(*info).best_r=adj_R; 
				(*info).best_f=f;
				(*info).best_sigma=sigma;
				toFile(model,"best_overall");}
			(*info).cycleCount++;		
		}
	}
	return;
}


int main(int argc, char *argv[]) {


    RInside R(argc, argv);              // create an embedded R instance 


	string fileName="";
	string degrees="2";
	cout<<"Please enter R workplace file name(do not inclue .RData)"<<endl;
	getline(cin,fileName);

 
    	string str= 
	//"load(file.choose(new= FALSE));"                //can have data file from typing in termal
	"load(\"/home/main/Documents/CSC423/"+fileName+".RData\");" //loads data file
	"ls();";

	//find the data.frame that will be used    
	string data="";	
	Rcpp::StringVector array = R.parseEval(str);
	data.append(array[1]);
	
	//get the headings
	str="attributes("+data+")$names;";
	Rcpp::StringVector headings=R.parseEval(str);
	
	//Print the Headings to the consule
	for(int i=0;i<headings.size();i++){
		if(i==0){cout<<"Response variable: ";}
		else{cout<<"Explanatory variable: ";}	
		cout<<headings[i]<<endl;
	}

	
	//build and send the string to get the compelete secound order model as a vector of strings
	str="sec_order=poly(";
	for(int i=1;i<headings.size();i++){
		str.append(data+"$"+headings[i]+", ");
	}
	str.append("degree="+degrees+"); attributes(sec_order)$dimnames[[2]];");	
	Rcpp::StringVector powerOf =R.parseEval(str);
	
	
	//builds the explanatory strut from the string of numbers
	Explanatory inputs[(headings.size()-1)*atoi(degrees.c_str())];
	
	
	//findstoping point for while loop	
	stringstream ss2;
	ss2<<headings[headings.size()-1]<<"^"+degrees+" ";
	string stop=ss2.str();

	
	//builds a vector of strings of each variable
	int k=0;
	int num=0;
	while(str.compare(stop)!=0){
	
		stringstream ss;
		ss<<powerOf[k];
		string s=ss.str();
		str="";
		
		//finds the total numeber of input variables	
		int count=0;
		for(int i=0;i<powerOf[k].size();i++){if(i%2!=0){continue;}else{if(s.at(i)!='0'){count++;}}}
		//builds the string

		if(count==1){
			for(int i=0;i<powerOf[k].size();i++){
				if(i%2!=0 || s.at(i)=='0')continue;
				else{
					if(s.at(i)=='1'){str.append(headings[(i/2)+1]);}
					//if the number is higher than one need to take it too that power
					else{str.append(headings[(i/2)+1]);str.append("^");str.append(s.substr(i,1));str.append(" ");}
					//muiltipule inputs need to mulitple between them but cant have at last one	
					if(count!=1){str.append(" * ");}
					count--;
				}		
			}
			inputs[num].name=str;
			num++;
		}
		k++;
	}

	//builds struct to pass info to function findBest
	InfoPass info;
	info.data=data;
	info.response=headings[0];
	info.size=num;
	info.f_value=0;
	info.best_f=0;
	info.adj_R_squared=0;
	info.best_r=0;
	info.sigma=numeric_limits<double>::max();
	info.best_sigma=0;
	info.cycleCount=0;
	info.total_test=pow(atof(degrees.c_str()),(num+1))-atof(degrees.c_str());

	//finds highest f and prints its regression model(call in R) to file model.txt 
	cout<<"Total number of variables: "<<num<<endl;	
	findBest(num,inputs,&R,&info);
	
	toFile("The best final results:\n\n","final_output.txt"),
	makeOutput("best_f_value");
	makeOutput("best_adj_R_squared");
	makeOutput("best_sigma");
	makeOutput("best_overall");	
	cout<<"Number of models tested: "<<info.cycleCount<<endl;
    

    exit(0);
}



