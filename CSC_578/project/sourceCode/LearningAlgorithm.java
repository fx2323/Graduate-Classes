import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class LearningAlgorithm {
	private Hmm h;
	private Viterbi v;
	private BaumWelch b;
	private int type;
	
	public LearningAlgorithm(Hmm hmm, int type){
		this.h= hmm;
		this.type=type;
	}



	public int[] mostLikelySequence(int output[]) throws NumberFormatException, IOException {
		if(this.type==1){
			this.v=new Viterbi(this.h);
			return this.v.mostLikelySequence(output);
			}
		else if(this.type==2){
			
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			double threshold=Double.parseDouble(br.readLine());
			this.b = new BaumWelch(this.h, threshold);
			return this.b.mostLikelySequence(output);
		}
		else return output;
	}
}
