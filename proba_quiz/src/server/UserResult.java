package server;

public class UserResult implements Comparable<UserResult>{

	String username="";
	String subject="";
	int score=0;
	
	
	public UserResult() {
	}
	
	public UserResult(String username, String subject, int score) {
		this.username = username;
		this.subject = subject;
		this.score = score;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public int compareTo(UserResult arg0) {
		return this.score-arg0.score;
	}
	
	 public String toString() {
	        return "|User: " + username + " - " +"|"+ score;
	    }
	
	
	}
