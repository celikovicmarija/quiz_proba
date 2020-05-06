package client;

public class RegisteredUser{

	private String username;
	private int score;
	private String password;
	private String userPath;
	
	public String getUserPath() {
		return userPath;
	}

	public void setUserPath(String userPath) {
		this.userPath = userPath;
	}

	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RegisteredUser() {
	}
	
	public RegisteredUser(String username, String password){
		this.username = username;
		this.password = password;
		score=0;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
