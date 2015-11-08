import java.util.*;
import java.lang.*;
import java.text.*;
import java.io.*;

/**
 * This is a simple class for running your code.  You might want to
 * extend or modify this class, or write your own.  (But your code
 * should still work properly when run with this code.)
 **/
@SuppressWarnings("unused")
public class RunViterbi {
	
	//data used for error average
	private static int n=0;
	private static double errorAverage=0;

    /* won't print this HMM if more than this many states */
    private static final int MAX_STATES = 100;

    /* won't print an evidence table for this HMM if more than this
     * many outputs */
    private static final int MAX_OUTPUTS = 100;

    /**
     * A simple main that loads the dataset contained in the file
     * named in the first command-line argument, builds an HMM, prints
     * it out and finds and prints the most likely state sequence for
     * each of the output sequences.
     **/


	public static void main(String[] argv)
	throws FileNotFoundException, IOException {

	// get file name of dataset
	String file_name = "";
	try {
	    file_name = argv[0];
	} catch (Exception e) {
	    System.err.println("Arguments: <file_name>");
	    return;
	}

	// would use code if BaumWelch.java worked
	
//	System.out.println("1 for Viterbi\n2 for Baum-Welch");
//	System.out.println("Enter an Integer :");
//	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//	int settings=Integer.parseInt(br.readLine());
	int settings =1;
	
	// get data from given file
	DataSet ds = new DataSet( file_name);

	// build HMM from given data
		Hmm h = new Hmm(ds.numStates, ds.numOutputs,
			ds.trainState, ds.trainOutput,settings);
	

	// sets data to print to output.txt
	PrintStream original = System.out;
	try {
	    System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output.txt")), true));
	} catch (Exception e) {
	     e.printStackTrace();
	}
	// print HMM, unless too big
	if (ds.numStates <= MAX_STATES) {

	    System.out.println("Start probabilities:");
	    for (int s = 0; s < ds.numStates; s++)
		System.out.println(name(ds.stateName[s]) + ": " +
				   num(Math.exp(h.getLogStartProb(s))));

	    System.out.println();
	    System.out.println("Transition probabilities:");

	    System.out.print(name("") + " ");
	    for (int s = 0; s < ds.numStates; s++)
		System.out.print(" " + name(ds.stateName[s]));

	    System.out.println();
	    for (int s = 0; s < ds.numStates; s++) {
		System.out.print(name(ds.stateName[s]) + ":");
		for (int u = 0; u < ds.numStates; u++) {
		    System.out.print(" "
				   + num(Math.exp(h.getLogTransProb(s,u))));
		}
		System.out.println();
	    }

	    if (ds.numOutputs <= MAX_OUTPUTS) {

		System.out.println();
		System.out.println("Output probabilities:");

		System.out.print(name("") + " ");
		for (int o = 0; o < ds.numOutputs; o++)
		    System.out.print(" " + name(ds.outputName[o]));

		System.out.println();
		for (int s = 0; s < ds.numStates; s++) {
		    System.out.print(name(ds.stateName[s]) + ":");
		    for (int o = 0; o < ds.numOutputs; o++) {
			System.out.print(" "
				   + num(Math.exp(h.getLogOutputProb(s,o))));
		    }
		    System.out.println();
		}
	    }
	}

	// create LearningAlgorithm object for computing most likely sequences and learning
	//LearningAlgorithm la =new LearningAlgorithm(h,settings);
	
	System.out.println();

	// compute and print most likely sequence for each test sequence
	for (int i = 0; i < ds.testOutput.length; i++) {
		LearningAlgorithm la =new LearningAlgorithm(h,settings);
	    int[] state = la.mostLikelySequence(ds.testOutput[i]);
	    int errors = 0;

	    System.out.println();
	    System.out.println("sequence "+i+":");
	    for (int j = 0; j < state.length; j++) {
		System.out.println(ds.stateName[ds.testState[i][j]]+"\t"+
				   ds.stateName[state[j]] +"\t"+
				   ds.outputName[ds.testOutput[i][j]]);
		if (state[j] != ds.testState[i][j])
		    errors++;
	    }
	    System.out.println("errors: " + errors + " / " + state.length +
			       " = " + ((double) errors)/state.length);
	    
	    //add new item to running average
		errorAverage=((n*errorAverage+(((double) errors)/state.length))/(n+1));
		n++;	    

	}
    //print to command line
    System.setOut(original);
    System.out.println("Average of errors: "+errorAverage);
	 


    }

    // private print formatting stuff
    private static NumberFormat nf = new DecimalFormat("#.000");

    private static String name(String s) {
	return (s + "    ").substring(0, 4);
    }

    private static String num(double d) {
	return nf.format(d);
    }

}
	    
