package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client implements Runnable {

	static Socket communicationSocket = null;
	static BufferedReader serverInput = null;
	static PrintStream serverOutput = null;
	static BufferedReader keyboardInput = null;

public static void main(String[] args){
		
		try {
			communicationSocket = new Socket("localhost",9000);
			serverInput = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
			serverOutput = new PrintStream(communicationSocket.getOutputStream());
			keyboardInput = new BufferedReader(new InputStreamReader(System.in));
			
			new Thread(new Client()).start();
			
			String input;
			
			while(true) {
				input = serverInput.readLine();
				System.out.println(input);
				
				if(input == null || input.startsWith(">>>Goodbye"))
					break;
			}
			
			communicationSocket.close();
			
		} catch (SocketException e) {
			System.out.println("Server got disconnected!");
		} catch (UnknownHostException e) {
			System.out.println("Unknown host!");
		} catch (IOException e) {
			System.out.println("Server is down!");
		}
		
	}
	
	
	@Override
	public void run() {
		
		String message;
		
		while(true) {
			try {
				message = keyboardInput.readLine();
				serverOutput.println(message);
				
				if(message.equals("exit"))
					break;
				
			} catch (SocketException e) {
				System.out.println("Server got disconnected!");
			} catch (IOException e) {
				System.out.println("Error");
			}
			
			
		}
		
	}
}
