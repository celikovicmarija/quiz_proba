package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import client.RegisteredUser;
import client.User;

public class ClientHandler extends Thread{
	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket communicationSocket = null;
	User user = new User();
	RegisteredUser ruser= new RegisteredUser();
	public static String activeUsersResultFile ="/Users/Marija/eclipse-workspace/proba_quiz/db/active_users_results.txt";
	public static String usersAll ="/Users/Marija/eclipse-workspace/proba_quiz/db/users.txt";
	public static String pathToUserResults="/Users/Marija/eclipse-workspace/proba_quiz/db/";
	
	public ClientHandler(Socket communicationSocket) {
		this.communicationSocket = communicationSocket;
	}
	
	@Override
	public void run() {
		
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			printFirstMenu();
			
			if(Server.activeUsers.contains(user)) {
				
				boolean signal = true;
				
				while(signal) {
					signal = printQuizMenu();
					
					if(signal == false) {
						exit();
						break;
					}
				}
				
			}
			if(Server.activeRUsers.contains(ruser)) {
				
				while(true) {
					boolean signal = printQuizMenuRegisteredUsers();
					
					if(signal == false) {
						exit();
						break;
					}else {
						continue;
					}
				}
				
			}else {
				clientOutput.println(">>>Error, user has not beed added to the appropriate list successfully!");
			}
			
			
		} catch (IOException e) {
			clientOutput.println(">>>Error in the run method of Client Handler!");
			Server.activeUsers.remove(user);
			Server.activeRUsers.remove(ruser);
			Server.users.remove(this);
			
			
		}

	}
	public void printFirstMenu() {
		
		clientOutput.println(">>>Welcome :)");
		clientOutput.println(">>>Let me explain you how this game works...");
		try {
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+1000);
			clientOutput.println(">>>If you login as a guest, you may enjoy only the preview or our mighty app.");
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+2000);
			clientOutput.println(">>>But if you register, you'll be able to take different types of quizzes, and see your playing history when you come back to our site.");
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+3500);
			clientOutput.println(">>>Choose wisely!");
			clientOutput.println();
		} catch (InterruptedException e1) {
			clientOutput.println(">>>Whoops, fix the sleep timer.");
		}
		clientOutput.println(">>>Please enter the number: ");
		clientOutput.println("1 - Play as a guest"); 
		clientOutput.println("2 - Login");
		clientOutput.println("3 - Registration");
		clientOutput.println("4 - Exit");
		
		try {
			String index = clientInput.readLine();
			
			if(index != null) {
				
				switch(index) {
					case "1": {
						login_guest();
						break;
					}
					case "2": {
						login();
						break;
					}
					case "3": {
						registration();
						break;
					}
					case "4": {
						exit();
						break;
					}
					default:  {
						clientOutput.println(">>>Error, you entered the inappropriate value!");
					}
				}
				
			}else {
				Server.users.remove(this);
				clientOutput.println(">>>Goodbye!");
				communicationSocket.close();
			}
			
		} catch (IOException e) {
			clientOutput.println(">>>Error while running the first menu!");
		}
	}
	
	public boolean printQuizMenu() {
		
		clientOutput.println(">>>Please enter the number: ");
		clientOutput.println("1 - Take the quiz");
		clientOutput.println("2 - Exit");
	
		
		boolean signal = false;
		
		try {
			String index = clientInput.readLine();
			
			if(index != null) {
				
				switch(index) {
					case "1": {
						signal = true;
							playQuiz();
							break;
						}
					case "2": {
						signal = false;
						exit();
						break;
					}
					default:  {
						clientOutput.println(">>>Error, you entered the inappropriate value!");
						signal = false;
					}
				}
				
			}else {
				Server.users.remove(this);
				clientOutput.println(">>>Goodbye!");
				communicationSocket.close();
			}
			
		} catch (SocketException e) {
			System.out.println(">>>Client got disconnected!");
		} catch (IOException e) {
			clientOutput.println(">>>Error in the quiz menu!");
		} 
		return signal;
	}
	public int selectSubject(){
	int signal = -1;
		
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			clientOutput.println(">>>Please select the number: ");
			clientOutput.println("1 - Geography");
			clientOutput.println("2 - Computer science");
			clientOutput.println("3 - History");
			clientOutput.println("4 - Mixed quiz");
			
			String index = clientInput.readLine();
			
				switch(index) {
					case "1": {
						signal = 1;
						break;
					}
					case "2": {
						signal = 2;
						break;
					}
					case "3": {
						signal=3;
						break;
					}					
					case "4": {
						signal = 4;
						break;
					}
					case "5": {
						signal = 0;
						break;
					}
					default:  {
						clientOutput.println("Error!");
						signal = -1;
					}
				}
			
			
		}catch (SocketException e) {
			System.out.println("Client got disconnected!");
		} catch (IOException e) {
			clientOutput.println("Error in menu!");
		} 
		
		return signal;
		
	}
	public boolean printQuizMenuRegisteredUsers() {
	boolean signal = false;
		
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			clientOutput.println(">>>Please enter the number :)");
			clientOutput.println("1 - Select the subject");
			clientOutput.println("2 - Select the level");
			clientOutput.println("3 - Play the quiz");
			clientOutput.println("4 - Show your previous results");
			clientOutput.println("5 - Show the results of other online users");
			clientOutput.println("6 - Show sorted results by subject");
			clientOutput.println("7 - Logout and exit");

			String index = clientInput.readLine();
			int n=-1;
			int s=-1;
			String subject="general";
			int p=-1; 
					
				switch(index) {
					case "1": {
						while(s<0) {
							s = selectSubject();
							if(s==0) {
								exit();
								break;
							}else {
								signal=true;
								continue;
							}
						}
						switch(s) {
						case 1: {subject="geography";signal=true;break;}
						case 2: {subject="computers";signal=true;break;}
						case 3: {subject="history"; signal=true;break;}
						case 4: {
							subject="general";
							signal=true;
						}
						default:  {
							clientOutput.println("Error!");
						}
						}
						
					}
					case "2": {
						while(n<0) {
							n = selectLevel();
							if(n==0) {
								exit();
								break;
							}else {
								signal=true;
								continue;
							}
						}
					}
					case "3": {
						if(n<0) {
							playQuizRegistered(5, subject);
							signal = true;
							break;
						} else {
							playQuizRegistered(n, subject);
							signal = true;
							break;
						}
					
						
					}
					case "4": {
						Connector.showUsersResults(clientOutput,pathToUserResults + ruser.getUsername()+".txt",1);
						signal = true;
						break;
					}
					case "5": {
						Connector.showUsersResults(clientOutput,activeUsersResultFile, 0);
						signal = true;
						break;
					}
					case "6": {
						while(p<0) {
							p = selectSubject();
							if(p==0) {
								exit();
								break;
							}else {
								signal=true;
								continue;
								
							}
						}
						
						switch(p) {
						case 1: {
							Connector.showResultOnASubject(clientOutput,"geography");
							break;}
						case 2: {
							Connector.showResultOnASubject(clientOutput,"computers");
							break;}
						case 3: {
							Connector.showResultOnASubject(clientOutput,"history");
							break;
							}
						case 4: {				
							Connector.showResultOnASubject(clientOutput,"general");
							break;
						}
						default:  {
							clientOutput.println("Error!");
							
							}
						}
						if (p>0&& p<=4)
							signal=true;
						
					}
					case "7": {
<<<<<<< Updated upstream
						if(p<1 || p>4)
						 {
							
=======
					
						if(p<1 || p>4)
						 {
>>>>>>> Stashed changes
							exit();
							signal = false;
							break;
						} else break;
						
					}
					default:  {
						clientOutput.println("Error!");
						signal = false;
					}
				}
			
			
		}catch (SocketException e) {
			System.out.println("Client got disconnected!");
		} catch (IOException e) {
			clientOutput.println("Error in menu!");
		} 
		
		return signal;
	}
	public int selectLevel() {
		int signal = -1;
		
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			clientOutput.println(">>>Please enter the number :)");
			clientOutput.println("1 - Easy");
			clientOutput.println("2 - Medium");
			clientOutput.println("3 - Hard");
			clientOutput.println("4 - Logout and exit");
			
			String index = clientInput.readLine();
			
			
				switch(index) {
					case "1": {
						signal=5;
						break;
						}
					case "2": {
						signal=10;
						break;
					}
					case "3": {
						signal=15;
						break;
					}
					case "4": {
						signal = 0;
						break;
					}
					default:  {
						clientOutput.println("Error!");
						signal = -1;
					}
				}
			
			
		}catch (SocketException e) {
			System.out.println("Client got disconnected!");
		} catch (IOException e) {
			clientOutput.println("Error in menu!");
		} 
		
		return signal;
	}

	public void exit() {
		
		try {
			clientOutput.println(">>>Please write 'exit': ");
			String message = clientInput.readLine();
			
			if(message != null && message.equals("exit")) {
				Server.activeUsers.remove(user);
				Server.users.remove(this);
				clientOutput.println(">>>Goodbye!");
				communicationSocket.close();
			} else {
				Server.activeUsers.remove(user);
				Server.users.remove(this);
				clientOutput.println(">>>User was removed!");
				communicationSocket.close();
			}
			
		}catch (SocketException e) {
			clientOutput.println(">>>Error: Communication socket has not been closed properly!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the client input in exit function!");
		}
		//Da automatski izbirse ceo sadryaj fajla u koji se upisuju rezultati
		//Nisam sigurna da radi
		if (Server.activeRUsers.size()==0 && Server.activeUsers.size()==0) {
			final File file = new File(activeUsersResultFile);
			try (PrintWriter writer = new PrintWriter(file))  {
			    writer.print("");                
			} catch (FileNotFoundException e) {
				System.out.println("Error while clearing leaderboard of active users!");
			}
		}
	}
	
	public void login_guest() {
		
		try {
			String username;
			boolean valid = false;
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			do {
				clientOutput.println(">>>Please enter the username: ");
				username = clientInput.readLine();
				
				if(username != null && username != "" && !username.contains(" ")) {
					valid = true;
					user.setUsername(username);
					user.setScore(0);
					if(!Server.activeUsers.contains(user))
						Server.activeUsers.add(user);
					else {
						clientOutput.println(">>>The user is already online!");
						return;
					}
					
					clientOutput.println(">>>Welcome " + username + " :)");
				}else {
					clientOutput.println(">>>Username cannot contain blank spaces and cannot be an empty string!");
				}
				
			}while(valid == false);
			
		}catch(Exception e) {
			clientOutput.println(">>>Error happened during the login process!");
		}
	}
	public void login() {
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			boolean isValid = false;
			String username;
			String password;
			int i=0;
			do {
				clientOutput.println(">>>Please enter the username: ");
				username = clientInput.readLine();
				clientOutput.println(">>>Please enter the password: ");
				password = clientInput.readLine();
				isValid = Connector.isTheUserRegistered(clientOutput,usersAll, username, password, ruser);
				
				if(isValid == true) {
					clientOutput.println(">>>Welcome " + username);
					ruser.setScore(0);
					ruser.setUsername(username);
					ruser.setPassword(password);
				if(!Server.activeRUsers.contains(ruser))
					Server.activeRUsers.add(ruser);
				else {
					clientOutput.println(">>>The user is already loged in!");
					return;
				}
					
				}else {
					clientOutput.println(">>>Requested user is not in the list! Please enter credentials again.");
					i++;
					if(i==3)
						break;
				}
				
			}while(!isValid);
			if(i==3) {
				clientOutput.println(">>>You have failed to log in three times.");
				clientOutput.println(">>>Would you like to proceed with registration? (yes/no)");
				String answer=clientInput.readLine();
				if(answer.toLowerCase().equals("yes")) {
					clientOutput.println(">>>Redirecting to registration, please wait...\n");
					TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+2200);

					registration();
				}
				else {
					clientOutput.println(">>>User didn't want to register thyself.");
					exit();
				}
			}
							
		} catch (IOException e) {
			clientOutput.println(">>>Error while communicating with the user.");
		} catch (InterruptedException e) {
			clientOutput.println(">>>Whoops, fix the sleep timer.");
		}
	}
<<<<<<< Updated upstream
	public boolean isTheUserRegistered(String file, String username, String password) {
		
		LinkedList<String> users = new LinkedList<>();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			boolean end = false;
			
			while(!end) {
				String s = in.readLine();
				if(s == null) {
					end = true;
				}else {
					users.add(s);
				}
			}
			in.close();
			
			for (String u : users) {
				if(u.startsWith(username)) {
					String[] userData = u.split(" ");
				if(password.startsWith(encrypt(userData[1], userData[0]))) {
						ruser.setUsername(userData[0]);
						ruser.setPassword(userData[1]);
						return true;
					}
					
				}
			}
			
			
		} catch (FileNotFoundException e) {
			clientOutput.println(">>>Error: File has not been found!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the file!");
		}
		
		return false;
	}
	
=======

>>>>>>> Stashed changes
	public void registration() {
		
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			boolean isValid = false;
			
			do {
				clientOutput.println(">>>Please enter the username: ");
				ruser.setUsername(clientInput.readLine());
				
				if(ruser.getUsername().contains(" ")) {
					clientOutput.println(">>>Username cannot contain blank spaces!");
				}else if(Connector.doesUsernameExist(clientOutput,ruser.getUsername())){
					clientOutput.println(">>>Username already exists! Please enter another one.");
				}else {
					isValid = true;
				}
				
			}while(!isValid);
			
			
			clientOutput.println(">>>Welcome " + ruser.getUsername());
			
				isValid = false;
			
			do {
				clientOutput.println(">>>Please enter the password: ");
				ruser.setPassword(clientInput.readLine());
				if(ruser.getPassword().length()<5) {
					clientOutput.println(">>>Password has to be longer than 5 characters! Please try again!");
				}else {
					isValid = true;
				}
				
			}while(!isValid);
			Server.activeRUsers.add(ruser);
			clientOutput.println(">>>Creating your file, please wait...");
		File file = new File(pathToUserResults + ruser.getUsername()+".txt");

			if(file.createNewFile()) {
				clientOutput.println(">>>File " + ruser.getUsername() + " is created.");
				clientOutput.println(">>>In it, we will save your results.");
				ruser.setUserPath(pathToUserResults + ruser.getUsername());
				Connector.writeUserInFile(clientOutput,ruser);
			}else {
				clientOutput.println(">>>File " + ruser.getUsername() + " was not created.");
			}
		} catch (IOException e) {
			System.out.println(">>>Error happened during registration!");;
		} 
	}
<<<<<<< Updated upstream
public void writeUserInFile(RegisteredUser ruser) {
	
	String passwordEncrypted=encrypt(ruser.getPassword(), ruser.getUsername());
	String newUserData = ruser.getUsername() + " " + passwordEncrypted + " " + ruser.getUserPath();
		
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter("/Users/Marija/eclipse-workspace/proba_quiz/db/users.txt", true)));
			pw.print(newUserData);
			pw.println();
			pw.close();
		} catch (IOException e) {
			clientOutput.println(">>>Error happened while writing the user in the Registered User's file!");
		}
	}
	public boolean doesUsernameExist(String name) {
		try {
			String filePath = "/Users/Marija/eclipse-workspace/proba_quiz/db/users.txt";
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			boolean end = false;
			
			while(!end) {
				String s = in.readLine();
				if(s == null) {
					end = true;
				}else if(s.startsWith(name)){
					in.close();
					return true;
				}
			}
			
			in.close();
			
		} catch (FileNotFoundException e) {
			clientOutput.println(">>>Error: File has not been found!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the file!");
		}
		return false;
	}
	public LinkedList<Question> readQuestionsFromFile(String filename){
		LinkedList<Question> listOfQuestions = new LinkedList<Question>();
		String filePath = "/Users/Marija/eclipse-workspace/proba_quiz/questions/"+ filename+".txt";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			boolean end = false;
			String textLine1, textLine2;
			
			while(!end) {
				textLine1 = in.readLine();
				textLine2 = in.readLine();
				if(textLine1 == null) {
					end = true;
				}else {
					Question q=	new Question(textLine1, textLine2);
					listOfQuestions.add(q);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			clientOutput.println(">>>Error: File has not been found!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the file!");
		}
		listOfQuestions=randomSample(listOfQuestions, listOfQuestions.size());
		return listOfQuestions;
	}
	static LinkedList<Question> randomSample(LinkedList<Question> list, int size) {
	    LinkedList<Question> sample = new LinkedList<>();

	    for (int sortedSampleIndices[] = new int[size], i = 0; i < size; i++) {
	        int index = rand.nextInt(list.size() - i);
=======
>>>>>>> Stashed changes

	public void printCurrentStatus() {
		
		Collections.sort(Server.activeUsers, new Comparator<User>() {
			@Override
			public int compare(User u1, User u2) {
				return u2.getScore() - u1.getScore();
			}
		});
		
		for(User u : Server.activeUsers) {
			clientOutput.println(u.getUsername() + " " + u.getScore());
		}
	}

	public void playQuiz() {
		Connector.quizAnimation(clientOutput);
		LinkedList<Question> questions = Connector.readQuestionsFromFile(clientOutput,"general");
		int j=1;
		for (Question q : questions) {
			try {
				clientOutput.println();
				String pom=" "+j+". " +q.getQuestion();
				clientOutput.println(pom);
				String answer = clientInput.readLine();
				j++;
				
				if(answer.toLowerCase().equals(q.getAnswer().toLowerCase())) {
					clientOutput.println(">>>Your answer was correct!");
					user.setScore(user.getScore() + 1);
				}else {
					clientOutput.println(">>>Your answer was incorrect! Correct answer is " + q.getAnswer() + ".");
				}
				
				clientOutput.println();
				clientOutput.println(">>>Current status of players that are online: ");
				
				printCurrentStatus();
				if(j==6)
					break;
				
			} catch (IOException e) {
				clientOutput.println(">>>Error while reading the question!");
			}
		}
		clientOutput.println();
		clientOutput.println(">>>You have compleated the quiz successfully!");
		try {
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+800);
			clientOutput.println(">>>Your final score is: " + user.getScore());
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+800);
			clientOutput.println(">>>Restarting your points...");	
			user.setScore(0);
			clientOutput.println();	
		} catch (InterruptedException e2) {
			clientOutput.println(">>>Whoops, fix the sleep timer.");
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+1000);
			clientOutput.println(">>>Now that you are familiar with our quiz...");
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+1200);
			clientOutput.println(">>>Maybe you've changed your mind.");
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+2200);
			clientOutput.println(">>>Would you like to register?");
			clientOutput.println();
			String answer=clientInput.readLine();
			if(answer.toLowerCase().equals("yes")) {
				clientOutput.println(">>>That's awesome! Redirecting to the registration, please wait...");
				TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+2200);
				registration();
			}
			
			else {
				clientOutput.println(">>>User didn't want to register thyself.\n");
			}
		} catch (InterruptedException e1) {
			clientOutput.println(">>>Whoops, fix the sleep timer.");
		} catch (IOException e) {
			clientOutput.println(">>>Error while communicating with the user.");

		}
		
	}

	
	public void playQuizRegistered(int n, String filename){
		LinkedList<Question> questions = Connector.readQuestionsFromFile(clientOutput,filename);
		Connector.quizAnimation(clientOutput);

		for (int i = 1; i < n+1; i++) {
			try {
			
				clientOutput.println();
				String pom=" "+i + "." + " "+ questions.get(i-1).getQuestion();
				clientOutput.println(pom);
				String answer = clientInput.readLine();
				
				if(answer.toLowerCase().equals(questions.get(i-1).getAnswer().toLowerCase())) {
					clientOutput.println(">>>Your answer was correct!\n");
					ruser.setScore(ruser.getScore() + 1);
				}else {
					clientOutput.println(">>>Your answer was incorrect! Correct answer is " + questions.get(i-1).getAnswer() + ".\n");
				}
				
				clientOutput.println();
				clientOutput.println(">>>Current status of players that are online: ");
				
				printCurrentStatus();
				
			} catch (IOException e) {
				clientOutput.println(">>>Error while reading the question!\n");
			}
		}
		clientOutput.println();
		clientOutput.println(">>>You have completed the quiz successfully!\n");
		clientOutput.println(">>>Your final score is: " + ruser.getScore());
		clientOutput.println(">>>Saving the results in the database....");
		clientOutput.println();	
		Connector.saveResults(clientOutput,filename,ruser);
		clientOutput.println(">>>Your points have been restarted!\n");
		ruser.setScore(0);

		
	}
		
<<<<<<< Updated upstream
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter("/Users/Marija/eclipse-workspace/proba_quiz/db/"+ruser.getUsername()+".txt", true)));
			pw.print(newUserData);
			pw.println();
			pw.close();
			
			PrintWriter pw1 = new PrintWriter(new BufferedWriter(
					new FileWriter("/Users/Marija/eclipse-workspace/proba_quiz/db/active_users_results.txt", true)));
			pw1.print(newUserData);
			pw1.println();
			clientOutput.println(">>>Results saved!");
			pw1.close();
		} catch (IOException e) {
			clientOutput.println(">>>Error happened while writing the results in user's file!");
		}
		
	}
	public static String encrypt(String msg, String key) {
		String result="";
		for (int i=0;i<msg.length();i++) {
			char c=(char)(key.charAt(i%key.length())^msg.charAt(i));
			result+=c;
		}
		return result;
	}
	
=======
>>>>>>> Stashed changes
}
