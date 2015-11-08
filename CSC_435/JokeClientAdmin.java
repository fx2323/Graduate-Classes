import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


/**
 * 1. Andrew Tillmann Jan 21 , 2015
 * 2. build 1.8.0_25
 * 3. First you need to get to the directory were JokeClientAdmin.java file is located. Then run "javac JokeClientAdmin.java" in your terminal. Follow by the command "java JokeClientAdmin". The client should now be up and running.
 * If you wanted a different server web address than the default value of localhost then type "java JokeClientAdmin myWebAddress". With myWebAddress being your desired server address.
 * 4. Example of a running session:
 * Andrew Tillmann's Joke Client, 1.8.

Using Server: localhost, Port: 52323
Type (quit) to end or 'joke-mode', 'proverb-mode' or 'maintenance-mode' to change JokeServer status: proverb-mode
Server change to proverb-mode
Type (quit) to end or 'joke-mode', 'proverb-mode' or 'maintenance-mode' to change JokeServer status: joke-mode
Server change to joke-mode
Type (quit) to end or 'joke-mode', 'proverb-mode' or 'maintenance-mode' to change JokeServer status: maintenance-mode
Server change to maintenance-mode
Type (quit) to end or 'joke-mode', 'proverb-mode' or 'maintenance-mode' to change JokeServer status: joke-mode
Server change to joke-mode
Type (quit) to end or 'joke-mode', 'proverb-mode' or 'maintenance-mode' to change JokeServer status: quit
Cancelled by user request.

 * 5. JokeClientAdmin.java and JokeServer.java they both are required since they communicate with each other. 
 * @author main
 * 
 */
public class JokeClientAdmin {
	private static int _port=52323;
	private static String _hostName;
	
	
	public static void main(String args[]) throws IOException{
		if(args.length < 1 ) _hostName="localhost"; //checks to see if the program was feed an argument if so set it as the host server
		else _hostName=args[0];
		
		System.out.println("Andrew Tillmann's Joke Client, 1.8.\n"); //just a print out to give the user information the 1.8 is the java version followed by the server to connect to and the port that will be used
		System.out.println("Using Server: "+_hostName+", Port: "+Integer.toString(_port));
		
		BufferedReader stdIn= new BufferedReader(new InputStreamReader(System.in)); //buffer used that gets information from the users keyboard.
		while(true){
			System.out.print("Type (quit) to end or 'joke-mode', 'proverb-mode' or 'maintenance-mode' to change JokeServer status: "); 
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
					out.println(input);
					
					String line;
					while((line=in.readLine())!=null){ //if the data received back by the server is not empty then print the information till the data is empty
						System.out.println(line);
					}
					
					clientsocket.close();
				}catch(IOException e){

					e.printStackTrace(); //if error occurred while connecting to the server getting information from it or closing the connection the error stack is printed
				}
			}
		}
	}
}
