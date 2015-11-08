
/** This is a template for an HMM class.  Fill in code for the
 * constructor and all of the methods.  Do not change the signature of
 * any of these, and do not add any other public fields, methods or
 * constructors (but of course it is okay to add private stuff).  All
 * public access to this class must be via the constructor and methods
 * specified here.
 */
public class Hmm {
	
	//my code here:
	
	private double start_probabilities[];
	private double transition_probabilities[][];
	private double output_probabilities[][];
	private int numberOfStates;
	private int numberOfOutputs;
	private int mat_state[][];
	private int mat_output[][];
	private int order;
    /** Constructs an HMM from the given data.  The HMM will have
     * <tt>numStates</tt> possible states and <tt>numOutputs</tt>
     * possible outputs.  The HMM is then built from the given set of
     * state and output sequences.  In particular,
     * <tt>state[i][j]</tt> is the <tt>j</tt>-th element of the
     * <tt>i</tt>-th state sequence, and similarly for
     * <tt>output[i][j]</tt>.
     */
    public Hmm(int numStates, int numOutputs,
	       int state[][], int output[][],int order) {
    	//my code here
    	this.order=order;
    	this.numberOfOutputs=numOutputs;
    	this.numberOfStates=numStates;
    	this.mat_output=output;
    	this.mat_state=state;
    	this.start_probabilities=new double[numStates];
    	this.transition_probabilities=new double[numStates][numStates];
    	this.output_probabilities = new double[numStates][numOutputs];
       
    	for(int i=0;i<numStates;i++){
        	this.start_probabilities[i]=startProb(i);
        	for(int j=0;j<numStates;j++){
        		this.transition_probabilities[i][j]=transProb(i, j);
        	}
        	for(int j=0;j<numOutputs;j++){
        		this.output_probabilities[i][j]=outputProb(i, j);
        	}
        }
    }
    
    
    
    private double startProb(int state){
    	
    	// My code here
    	if(this.order==1){
    	double numerator=0;
    	double denominator=0;
    	
    	for(int i=0; i<this.mat_state.length;i++){
    		for(int j=0;j<this.mat_state[i].length;j++){
			if(this.mat_output[i][j]==1){
				denominator++;
				if(this.mat_state[i][j]==state){numerator++;}	
			}
    		}
    	}
    	return (Math.log(numerator/denominator));
    	}
    	return -1;
    }
    
    private double transProb(int fromState, int toState){
    	// my code here uses Laplace's law of succession
    	if(this.order==1){
		    double numerator=1;
		    double denominator=2;
		    
		    for(int i=0; i<this.mat_state.length;i++){
				for(int j=0;j<this.mat_state[i].length-1;j++){
				if(this.mat_state[i][j]==fromState){
					denominator++;
					if(this.mat_state[i][j+1]==toState){numerator++;}	
				}
				}
			}
		    return (Math.log(numerator/denominator));
	    	}
    	return -1;
    }
    
    private double outputProb(int state, int output){
    	// my code here uses Laplace's law of succession
    	if(this.order==1){
        double numerator=1;
        double denominator=2;
        for(int i=0; i<this.mat_state.length;i++){
    		for(int j=0;j<this.mat_state[i].length;j++){
    		if(this.mat_state[i][j]==state){
    			denominator++;
    			if(this.mat_output[i][j]==output){numerator++;}	
    		}
    		}
    	}
        return (Math.log(numerator/denominator));
    	}
    	return -1;

    }
    
    
    /** Returns the number of states in this HMM. */
    public int getNumStates() {
    	//my code here
    	return this.numberOfStates;
    }

    /** Returns the number of output symbols for this HMM. */
    public int getNumOutputs() {
    	//my code here
    	return this.numberOfOutputs;
    }

    /** Returns the log probability assigned by this HMM to a
     * transition from the dummy start state to the given
     * <tt>state</tt>.
     */
    public double getLogStartProb(int state) {
    	// My code here
    	return this.start_probabilities[state];
    }
    
    //sets new value for a state probability 
    public void setLogStartProb(int state, double newValue){
    	this.start_probabilities[state]=newValue;
    }

    /** Returns the log probability assigned by this HMM to a
     * transition from <tt>fromState</tt> to <tt>toState</tt>.
     */
    public double getLogTransProb(int fromState, int toState) {
    	// My code here
    	return this.transition_probabilities[fromState][toState];
    }
    
    //sets new value for a state probability
    public void setLogTransProb(int fromState, int toState, double newValue){
    	this.transition_probabilities[fromState][toState]=newValue;
    }

    /** Returns the log probability of <tt>state</tt> emitting
     * <tt>output</tt>.
     */
    public double getLogOutputProb(int state, int output) {
    	return this.output_probabilities[state][output];
    }
    
    public void setLogOutputProb(int state, int output, double newValue){
    	this.output_probabilities[state][output]=newValue;
    }
    
}
