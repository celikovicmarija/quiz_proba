package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import client.RegisteredUser;

public class Connector {
	
	static Random rand = new Random();
	public static void quizAnimation(PrintStream clientOutput) {
		clientOutput.println(">>>Okay, quiz loading, please wait...");
		try {
			TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+1000);
		} catch (InterruptedException e2) {
			clientOutput.println(">>>Whoops, fix the sleep timer.");

		}
	}
	public  static void showUsersResults(PrintStream clientOutput,String file, int i){

		if(i==1)
		clientOutput.println(">>>Okay, fetching results for the user... \n");
		else 
			clientOutput.println(">>>Okay, fetching results of online users... \n");

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			boolean end = false;
			String text = "";
			
			while(!end) {
				String s = in.readLine();
				if(s == null) {
					end = true;
				}else {
					text = text + s + " \n";
				}
			}
			in.close();
			clientOutput.println(text);
		} catch (FileNotFoundException e) {
			clientOutput.println(">>>Error: No result has not been found!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while showing the results!");
		}
	}
	public static void saveResults(PrintStream clientOutput,String filename, RegisteredUser ruser) {
		String newUserData = ruser.getUsername() + " "+filename+" " +ruser.getScore();
		
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
	public static void writeUserInFile(PrintStream clientOutput,RegisteredUser ruser) {
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
	static String encrypt(String msg, String key) {
		String result="";
		for (int i=0;i<msg.length();i++) {
			char c=(char)(key.charAt(i%key.length())^msg.charAt(i));
			result+=c;
		}
		return result;
	}
	public static LinkedList<Question> readQuestionsFromFile(PrintStream clientOutput,String filename){
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

	        int j = 0;
	        for (; j < i && index >= sortedSampleIndices[j]; j++)
	            index++;
	        sample.add(list.get(index));

	        for (; j <= i; j++) {
	            int temp = sortedSampleIndices[j];
	            sortedSampleIndices[j] = index;
	            index = temp;
	        }
	    }

	    return sample;
	}
	public static boolean  doesUsernameExist(PrintStream clientOutput,String name) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(ClientHandler.usersAll));
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
	public static boolean isTheUserRegistered(PrintStream clientOutput,String file, String username, String password, RegisteredUser ruser) {
		
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
					if(password.startsWith(Connector.encrypt(userData[1], userData[0]))) {
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
	public static void showResultOnASubject(PrintStream clientOutput,String sub) {
		clientOutput.println("<<<Okay, preparing the leaderboard...\n");
		LinkedList<String> rows = new LinkedList<>();
		LinkedList<UserResult> results= new LinkedList<UserResult>();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(ClientHandler.activeUsersResultFile));
			boolean end = false;
			
			while(!end) {
				String s = in.readLine();
				if(s == null) {
					end = true;
				}else {
					rows.add(s);
				}
			}
			in.close();
			for (String r : rows) {
		
					String[] resultRow = r.split(" ");
					resultRow.toString();
					if(sub.equals(resultRow[1])) {
						UserResult ur= new UserResult();
						ur.setSubject(sub);
						ur.setUsername(resultRow[0]);
						ur.setScore(Integer.parseInt(resultRow[2]));
						results.add(ur);
				}
					
			}
			clientOutput.println(">>>Preparing results for the subject: "+ sub);

			clientOutput.println("\n>>>Here is the leaderboard:\n");
			try {
				TimeUnit.MILLISECONDS.sleep((int)(Math.random()*300)+1000);
			} catch (InterruptedException e) {
				clientOutput.println(">>>Whoops, fix the sleep timer.");

			}

			clientOutput.println("__________________________");
			Collections.sort(results, new ScoreSorter());
			int i=1;
			for (UserResult userResult : results) {
				clientOutput.println("|"+i +userResult);
				i++;
			}
			
			clientOutput.println();
			
			
		} catch (FileNotFoundException e) {
			clientOutput.println(">>>Error: File has not been found!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the file!");
		}
		
	}

}