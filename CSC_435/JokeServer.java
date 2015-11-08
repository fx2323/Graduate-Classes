import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * 1. Andrew Tillmann Jan 21 , 2015
 * 2. build 1.8.0_25
 * 3. First you need to get to the directory were InetServer.java file is located. Then run "javac JokeServer.java" in your terminal. Follow by the command "java JokeServer". The server should now be up and running.
 * 4. Example of a running session:
 * Andrew Tillmann's Joke Client Server 1.8 starting up, listening at port 42323.

Andrew Tillmann's Joke Admin Server 1.8 starting up, listening at port 52323.

Give me something
Give me something
Give me something
Give me something
Give me something
Give me something
Give me something


 * 5. JokeClient.java and JokeClientAdmin.java they both are required since they communicate with the server to test full functionality
 * @author main
 */
class Worker extends Thread { //this worker class extends the thread class thus enabling multithreading 
	private Socket _sock;
	private static int _status; // 0 for joke, 1 for proverb, 2 for maintenance-mode
	private static Vector<String> _messages;
	
	Worker(Socket s ,int status,Vector<String> messages){ //the worker is assigned the connecting clients socket
		_sock=s;
		_status=status;
		_messages=messages;
	}
	
	public void run(){
		PrintStream out=null;
		BufferedReader in=null;
		
		
		try{
			in = new BufferedReader(new InputStreamReader(_sock.getInputStream())); //connects to the client to get information from it and buffer the data
			out = new PrintStream(_sock.getOutputStream()); //connects to the client to send information to it
			try{
				String message;
				message=in.readLine(); //gets message 'Give me something' from the client
				System.out.println(message); // prints out message on users terminal
				String clientName=in.readLine();
				out.println(_status); //sends the client the current mode the server is in
				if (in.readLine().equals("GetData")){ //if the client finished the random jokes/proverbs and needs to refresh the stack data is sent again 
					sendSomething(out,clientName);
				};
			}catch(IOException e){
				//if any errors occur connecting to the client and getting data and sending it back this message, "Server read error" will read on the users terminal and a error stack trace will be printed
				System.out.println("Server read error");
				e.printStackTrace();
			}
			
			_sock.close(); //closes the connection and terminate the thread
			
		}catch(IOException e){

			System.out.println(e); //if cannot connect to the client or close the connection this error will print
		}
	}
	
	static void sendSomething(PrintStream out, String clientName){
		if(_status==0 || _status==1){ //if message is joke or proverb send all the messages to the client with users name in them
			for( int i=0;i<_messages.size();i+=2){
				out.println(_messages.get(i)+clientName+_messages.get(i+1));
			}
		}
		else if(_status==2){
			out.println("The server is temporarily unavailable -- check-back shortly."); //sends message of maintenance-mode
		}
	}
	

}




public class JokeServer {
	static volatile int _status=0;
	
	
	static Vector<String> jokes = new Vector<String>(10,5);
	static Vector<String> proverbs = new Vector<String>(10,5);

	
	public static void main(String a[]) throws IOException{
		int q_len= 6;
		int admin_port=52323;
		int client_port=42323;
		
		
		//jokes taken from http://thoughtcatalog.com/christopher-hudspeth/2013/09/50-terrible-quick-jokes-thatll-get-you-a-laugh-on-demand/
		
		jokes.add("A: Hey ");
		jokes.add(", It's hard to explain puns to kleptomaniacs because they always take things literally.");
		jokes.add("B: Hey ");
		jokes.add(", If you want to catch a squirrel just climb a tree and act like a nut.");
		jokes.add("C: Hey ");
		jokes.add(", A magician was walking down the street and turned into a grocery store.");
		jokes.add("D: Hey ");
		jokes.add(", A blind man walks into a bar. And a table. And a chair.");
		jokes.add("E: Hey ");
		jokes.add(", A farmer in the field with his cows counted 196 of them, but when he rounded them up he had 200.");

		//proverbs are from http://gongwai.xaufe.edu.cn/englishonline/kwxx/proverbs/PROVERBS.HTM
		proverbs.add("A: Hey ");
		proverbs.add(", A bad penny always turns up.");
		proverbs.add("B: Hey ");
		proverbs.add(", A bird in the hand is worth two in the bush.");
		proverbs.add("C: Hey ");
		proverbs.add(", A chain is no stronger than its weakest link.");
		proverbs.add("D: Hey ");
		proverbs.add(", A fool and his money are soon parted.");
		proverbs.add("E: Hey ");
		proverbs.add(", A man's home is his castle.");
		
		
		
		new  modeServer(client_port,jokes,proverbs).start();//create a thread with status default
		

		
		System.out.println("Andrew Tillmann's Joke Admin Server 1.8 starting up, listening at port "+Integer.toString(admin_port)+".\n"); //message to user to inform them of the current settings
		
		Socket sock;
		
		
		try {
			
			@SuppressWarnings("resource")
			ServerSocket serverSock= new ServerSocket(admin_port,q_len);//a server socket is started at port 42323 with a queue length of 6
			
			
			while(true){
				sock = serverSock.accept(); //waits and listens at current port. If a request is received it is passed to the Worker thus starting a new thread. ModeServer goes back to listing at its port while Worker deals with the client. 
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream())); //makes a connection to the servers to get information from it and buffer the data
				PrintStream out = new PrintStream(sock.getOutputStream()); //makes a connection that the client will send information to the server
				changeStatus(in.readLine(),out); //changes the status of the mode to current status
				sock.close(); //closes the current connection goes back to listening for new connection
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} 
		

	}

	private static void changeStatus(String newStatus,PrintStream out) {
		
		if (newStatus.equals("joke-mode")){
			_status=0;
			out.println("Server change to joke-mode");
		}
		else if(newStatus.equals("proverb-mode")){
			_status=1;
			out.println("Server change to proverb-mode");
		}
		else if (newStatus.equals("maintenance-mode")){
			_status=2;
			out.println("Server change to maintenance-mode");
		}
		else{
			out.println("Server not changed please enter 'joke-mode' or 'proverb-mode' or 'maintenance-mode'.");
		}
		
	}
	
	
	
	static class modeServer extends Thread{
		int _port;
		int q_len=6;
		
		
		modeServer(int port, Vector<String> jokes, Vector<String> proverbs){
			_port=port;
			System.out.println("Andrew Tillmann's Joke Client Server 1.8 starting up, listening at port "+Integer.toString(_port)+".\n"); //message to user to inform them of the current settings
		}
		public void run(){
			Socket sock;
			try {
				
				@SuppressWarnings("resource")
				ServerSocket serverSock= new ServerSocket(_port,q_len);//a server socket is started at port 42323 with a queue length of 6
				while(true){
					sock = serverSock.accept(); //waits and listens at current port. If a request is received it is passed to the Worker thus starting a new thread. ModeServer goes back to listing at its port while Worker deals with the client. 
					if(_status==0){
						new Worker(sock,_status,jokes).start();
					}else{
						new Worker(sock,_status,proverbs).start();
					}
					
				}
			
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	
	
}




