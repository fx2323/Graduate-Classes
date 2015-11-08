/** This is a template for a Viterbi class, which can be used to
 * compute most likely sequences.  Fill in code for the constructor
 * and <tt>mostLikelySequence</tt> method.
 */
public class Viterbi {
	
	//my code here
	private Hmm hmm;
	private int[] myOutput;


    /** This is the constructor for this class, which takes as input a
     * given HMM with respect to which most likely sequences will be
     * computed.
     */
    public Viterbi(Hmm hmm) {

	// my code here
    	this.hmm=hmm;

    }

    /** Returns the most likely state sequence for the given
     * <tt>output</tt> sequence, i.e., the state sequence of highest
     * conditional probability given the output sequence, according to
     * the HMM that was provided to the constructor.  The returned
     * state sequence should have the same number of elements as the
     * given output sequence.
     */
    public int[] mostLikelySequence(int output[]) {

    	
	// my code here
    	this.myOutput= new int[output.length];
    	for(int i=0;i<output.length;i++){
    		withOutput(output,i);
    		}
    	
    	return this.myOutput;
    }
    
    //finds the max
    private void withOutput(int[] output,int index){
    	double max=0;
    	int maxIndex=0;
    	double current=0;
    	for(int i=0;i<this.hmm.getNumStates();i++){
	    	if(index==0){
	        	current=Math.exp(this.hmm.getLogStartProb(i)+ this.hmm.getLogOutputProb(i, output[index]));        
	    	}
	    	else{
	    		current=Math.exp(this.hmm.getLogTransProb(this.myOutput[index-1],i)+this.hmm.getLogOutputProb(i, output[index]));
	    		}
	    	if(current>max){max=current;maxIndex=i;}
    	}
    	this.myOutput[index]=maxIndex;
    	
    }
}
