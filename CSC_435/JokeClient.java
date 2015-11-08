import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;


/**
 * 1. Andrew Tillmann Jan 21 , 2015
 * 2. build 1.8.0_25
 * 3. First you need to get to the directory were JokeClient.java file is located. Then run "javac JokeClient.java" in your terminal. Follow by the command "java JokeClient". The client should now be up and running.
 * If you wanted a different server web address than the default value of localhost then type "java JokeClient myWebAddress". With myWebAddress being your desired server address.
 * 4. Example of a running session:
 * Example of a running session with jokes:

Andrew Tillmann's Joke Client, 1.8.

Using Server: localhost, Port: 42323
What is your name?
Andrew  
Type (quit) to end or hit enter for message: 
D: Hey Andrew, A blind man walks into a bar. And a table. And a chair.
Type (quit) to end or hit enter for message: 
B: Hey Andrew, If you want to catch a squirrel just climb a tree and act like a nut.
Type (quit) to end or hit enter for message: 
A: Hey Andrew, It's hard to explain puns to kleptomaniacs because they always take things literally.
Type (quit) to end or hit enter for message: 
C: Hey Andrew, A magician was walking down the street and turned into a grocery store.
Type (quit) to end or hit enter for message: 
E: Hey Andrew, A farmer in the field with his cows counted 196 of them, but when he rounded them up he had 200.
Type (quit) to end or hit enter for message: 
A: Hey Andrew, It's hard to explain puns to kleptomaniacs because they always take things literally.
Type (quit) to end or hit enter for message: 
C: Hey Andrew, A magician was walking down the street and turned into a grocery store.
Type (quit) to end or hit enter for message: 
E: Hey Andrew, A farmer in the field with his cows counted 196 of them, but when he rounded them up he had 200.
Type (quit) to end or hit enter for message: 
B: Hey Andrew, If you want to catch a squirrel just climb a tree and act like a nut.
Type (quit) to end or hit enter for message: 
D: Hey Andrew, A blind man walks into a bar. And a table. And a chair.
Type (quit) to end or hit enter for message: quit()
B: Hey Andrew, If you want to catch a squirrel just climb a tree and act like a nut.


Example of a running session with proverbs:

Andrew Tillmann's Joke Client, 1.8.

Using Server: localhost, Port: 42323
What is your name?
Andrew Tillmann
Type (quit) to end or hit enter for message: 
A: Hey Andrew Tillmann, A bad penny always turns up.
Type (quit) to end or hit enter for message: 
B: Hey Andrew Tillmann, A bird in the hand is worth two in the bush.
Type (quit) to end or hit enter for message: 
C: Hey Andrew Tillmann, A chain is no stronger than its weakest link.
Type (quit) to end or hit enter for message: 
E: Hey Andrew Tillmann, A man's home is his castle.
Type (quit) to end or hit enter for message: 
D: Hey Andrew Tillmann, A fool and his money are soon parted.
Type (quit) to end or hit enter for message: 
B: Hey Andrew Tillmann, A bird in the hand is worth two in the bush.
Type (quit) to end or hit enter for message: 
C: Hey Andrew Tillmann, A chain is no stronger than its weakest link.
Type (quit) to end or hit enter for message: 
D: Hey Andrew Tillmann, A fool and his money are soon parted.
Type (quit) to end or hit enter for message: 
E: Hey Andrew Tillmann, A man's home is his castle.
Type (quit) to end or hit enter for message: 
A: Hey Andrew Tillmann, A bad penny always turns up.

Example of a running session with both proverbs and jokes mixed then maintenance-mode at the end followed by closing the program:

Andrew Tillmann's Joke Client, 1.8.

Using Server: localhost, Port: 42323
What is your name?
Andy  
Type (quit) to end or hit enter for message: 
A: Hey Andy, It's hard to explain puns to kleptomaniacs because they always take things literally.
Type (quit) to end or hit enter for message: 
D: Hey Andy, A blind man walks into a bar. And a table. And a chair.
Type (quit) to end or hit enter for message: 
A: Hey Andy, A bad penny always turns up.
Type (quit) to end or hit enter for message: 
B: Hey Andy, A bird in the hand is worth two in the bush.
Type (quit) to end or hit enter for message: 
E: Hey Andy, A man's home is his castle.
Type (quit) to end or hit enter for message: 
B: Hey Andy, If you want to catch a squirrel just climb a tree and act like a nut.
Type (quit) to end or hit enter for message: 
E: Hey Andy, A farmer in the field with his cows counted 196 of them, but when he rounded them up he had 200.
Type (quit) to end or hit enter for message: 
C: Hey Andy, A magician was walking down the street and turned into a grocery store.
Type (quit) to end or hit enter for message: 
D: Hey Andy, A fool and his money are soon parted.
Type (quit) to end or hit enter for message: 
C: Hey Andy, A chain is no stronger than its weakest link.
Type (quit) to end or hit enter for message: 
A: Hey Andy, A bad penny always turns up.
Type (quit) to end or hit enter for message: 
The server is temporarily unavailable -- check-back shortly.
Type (quit) to end or hit enter for message: quit
Cancelled by user request.


 * 5. JokeClient.java and JokeServer.java they both are required since they communicate with each other. 
 * @author main
 *
 */
public class JokeClient {
	private static String _hostName;
	private static String _UserName;
	private static int _port=42323;
	
	private static Vector<String> jokes = new Vector<String>(5,5);
	private static Stack<String> jokesRandom = new Stack<String>();
	private static Vector<String> proverbs = new Vector<String>(5,5);
	private static Stack<String> proverbsRandom= new Stack<String>();
	
	
	public static void main(String args[]) throws IOException{
		if(args.length < 1 ) _hostName="localhost"; //checks to see if the program was feed an argument if so set it as the host server
		else _hostName=args[0];
		
		System.out.println("Andrew Tillmann's Joke Client, 1.8.\n"); //just a print out to give the user information the 1.8 is the java version followed by the server to connect to and the port that will be used
		System.out.println("Using Server: "+_hostName+", Port: "+Integer.toString(_port));
		
		
		
		
		

		System.out.println("What is your name?"); //Get asked name and send the name
		BufferedReader stdIn= new BufferedReader(new InputStreamReader(System.in)); //buffer used that gets information from the users keyboard. The data should be the users name
		_UserName=stdIn.readLine(); //user types in the name
		
		
		
		
		while(true){ 
			
			System.out.print("Type (quit) to end or hit enter for message: "); 
			String input=stdIn.readLine(); //reads the line from the user

			if( input.equals("quit")){ //if the line typed by the user is just "quit" the program closes. 
				System.out.println("Cancelled by user request.");
				break;
				}
			else{
				try{
					Socket clientsocket = new Socket(_hostName,_port); //establishes the connections endpoint 
					BufferedReader in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream())); //makes a connection to the servers to get information from it and buffer the data
					PrintStream out = new PrintStream(clientsocket.getOutputStream()); //makes a connection that the client will send information to the server
					out.println("Give me something"); //sends the server message to get something from it followed by the client's user name
					out.println(_UserName);
					
					int status=Integer.parseInt(in.readLine()); //gets status of the mode from the sever
					
					if(status==0 ){
						checkData(jokes,out,in);
						printMessage(status);
					}else if(status==1){
						checkData(proverbs,out,in);
						printMessage(status);
					}else{
						out.println("GetData");
						System.out.println(in.readLine()); //get and print maintenance message
					}
					
					
					clientsocket.close();
				}catch(IOException e){

					e.printStackTrace(); //if error occurred while connecting to the server getting information from it or closing the connection the error stack is printed
				}
			}
		}
	}
	

	private static void checkData(Vector<String> data,PrintStream out, BufferedReader in) throws IOException{
		if (data.isEmpty()){ //if current stack is empty send server message 'GetData' the data
			out.println("GetData");
			String line;
			while((line=in.readLine())!=null){
				data.add(line); //reads each new proverb or joke and adds to a vector
			}
		}else{
			out.println(""); //stack is not empty thus let server know no new data is needed
		}
	}
	
	private static void printMessage(int status){
		if(status==0){
			if(jokesRandom.isEmpty()){ //if random joke stack is empty fill it randomly from jokes vector
				fillStack(jokes,jokesRandom);
			}
			System.out.println(jokesRandom.pop()); //pops joke off stack and prints it
		}
		else if(status==1){
			if(proverbsRandom.isEmpty()){//if random proverb is empty fill it randomly from proverb vector
				fillStack(proverbs,proverbsRandom);
			}
			System.out.println(proverbsRandom.pop()); //pops proverb off stack and prints it
		}
	}


	private static void fillStack(Vector<String> vector,Stack<String> stack) {
		Random randomNumber = new Random();
		while(!vector.isEmpty()){ //while vector is not empty
			int index=randomNumber.nextInt(vector.size()); //finds random index within the size of the vector
			stack.add(vector.get(index)); //adds vector value to the stack
			vector.remove(index); //removes the vector value
		}
		
	};
	
}
