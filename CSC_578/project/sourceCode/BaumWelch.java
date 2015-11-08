
public class BaumWelch {
	Hmm hmm;
	double threshold;
	double[][] fwd;
	double[][] bkw;

	
	public BaumWelch(Hmm hmm, double threshold){
		this.hmm=hmm;
		//System.out.println(Math.exp(this.hmm.getLogStartProb(0)));
		this.threshold=threshold;
		
	}
	
	 public int[] mostLikelySequence(int[] output) {
		 
		//do baumWelch forward-backwards training
		 training(output);
		 
		//training is done now find best path
		Viterbi decoder= new Viterbi(this.hmm);
		return decoder.mostLikelySequence(output);

	 }
	 
	private void training(int[] output) {
		
		double probabilityLog;
		double oldProbabilityLog;
		do {
			//run forwards and backwards and save forwards probability
			 probabilityLog = forward_prob(output);
			 backward_prob(output);
			 oldProbabilityLog = probabilityLog;
			 
			 //temp variables
			double[][] transition_probabilities = new double[this.hmm.getNumStates()][this.hmm.getNumStates()];   
			double[][]  output_probabilities = new double[this.hmm.getNumStates()][this.hmm.getNumOutputs()];
	    
	        

	        for (int i=0; i<this.hmm.getNumOutputs(); i++) {
	        	for (int k=0; k<this.hmm.getNumStates(); k++){ 
	        		output_probabilities[k][output[i]] = this.fwd[i][k] + this.bkw[i][k]- probabilityLog;
	        		//System.out.println(Math.exp(output_probabilities[k][output[i]]));
	        	}
	        }
	        
	         for (int j=0; j<this.hmm.getNumStates(); j++){
	           for (int k=0; k<this.hmm.getNumStates(); k++){
	        	   transition_probabilities[j][k]=Double.NEGATIVE_INFINITY; // = log(0)
	           	for (int i=0; i<this.hmm.getNumOutputs()-1; i++){  
	            	transition_probabilities[j][k] =logplus(transition_probabilities[j][k], this.fwd[i][j] + hmm.getLogTransProb(j,k) + hmm.getLogOutputProb(k,output[i]) + this.bkw[i+1][k] - probabilityLog);
	            	//System.out.println(Math.exp(transition_probabilities[j][k]));
	           	}
	          }
	        }
	        
	      
	      // update hmm
	      for (int k=0; k<this.hmm.getNumStates(); k++) {
	        
	    	  double transSum = Double.NEGATIVE_INFINITY; // = log(0)
	    	  for (int ell=0; ell<this.hmm.getNumStates(); ell++){ 
	    		  transSum =logplus(transSum, transition_probabilities[k][ell]);
	    	  }
	    	  
	    	  for (int ell=0; ell<this.hmm.getNumStates(); ell++){ 
	        	this.hmm.setLogTransProb(k, ell, (transition_probabilities[k][ell] -transSum));
	        	 //System.out.println(Math.exp(this.hmm.getLogTransProb(k, ell)));
	    	  }
	        
	    	  double outputSum =Double.NEGATIVE_INFINITY; // = log(0)
	    	  for (int b=0; b<this.hmm.getNumOutputs(); b++){ 
	    		  outputSum =logplus(outputSum, output_probabilities[k][b]);
	    	  }
	        
	    	  for (int b=0; b<this.hmm.getNumOutputs(); b++){ 
	          this.hmm.setLogOutputProb(k,b,(output_probabilities[k][b] -outputSum));
	          //System.out.println(Math.exp(this.hmm.getLogOutputProb(k, b)));
	    	  }
	      }
	      
	      
	      // update log_probability
	      probabilityLog = forward_prob(output);

	      
	    } while (Math.abs(Math.exp(oldProbabilityLog)- Math.exp(probabilityLog)) > this.threshold);
		
	}

	private double forward_prob(int[]observations){
		this.fwd= new double [observations.length][this.hmm.getNumStates()];
		double prevous_sum;	
		
		for(int i=0; i<observations.length;i++){
			for( int j=0; j<this.hmm.getNumStates();j++){
				if(i==0){
					prevous_sum=this.hmm.getLogStartProb(j);
				}
				else{
					 prevous_sum =Double.NEGATIVE_INFINITY; // = log(0)
					for(int k=0;k<this.hmm.getNumStates();k++){
						prevous_sum = logplus(prevous_sum,this.fwd[i-1][k]+this.hmm.getLogTransProb(k, j));
						//System.out.println(Math.exp(this.fwd[i-1][k]+this.hmm.getLogTransProb(k, j)));
					}
					//System.out.println(Math.exp(prevous_sum));
				}
				this.fwd[i][j]=this.hmm.getLogOutputProb(j, observations[i])+prevous_sum;
				//System.out.println(Math.exp(this.fwd[i][j]));
			}
		}
		
		double probabilityLog=Double.NEGATIVE_INFINITY; // = log(0)
		for(int i=0;i<this.hmm.getNumStates();i++){
			probabilityLog=logplus(probabilityLog,this.fwd[observations.length-1][i]);
		}
		
		//System.out.println(Math.exp(probabilityLog));
		return probabilityLog;
	}
	
	
	private double backward_prob(int[]observations){
		this.bkw= new double [observations.length][this.hmm.getNumStates()];
		
		for(int i=(observations.length-1); i>0;i--){
			for( int j=0; j<this.hmm.getNumStates();j++){
				if(i==(observations.length-1)){
					this.bkw[i][j]=this.hmm.getLogTransProb(j,this.hmm.getNumStates()-1);
				}
				else{
					double sum=Double.NEGATIVE_INFINITY; // = log(0)
					for(int k=0;k<this.hmm.getNumStates();k++){
						sum = logplus(sum,this.hmm.getLogTransProb(j, k)+this.hmm.getLogOutputProb(k, observations[i])+this.bkw[i+1][k]);
					}
					this.bkw[i][j]=sum;
					//System.out.println(this.bkw[i][j]);
				}
			}
		}
		
		double probabilityLog=0; // = log(0)	
		for(int i=0;i<this.hmm.getNumStates();i++){
			probabilityLog=logplus(probabilityLog,this.hmm.getLogStartProb(i)+this.hmm.getLogTransProb(i, observations[0])+this.bkw[0][i]);
		}
		return probabilityLog;
	}
	
	  static double logplus(double one, double two) {

		    
		    return Math.log(Math.exp(one)+Math.exp(two));
		  }
		

}
