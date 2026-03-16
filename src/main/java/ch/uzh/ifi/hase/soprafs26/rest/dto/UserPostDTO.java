package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class UserPostDTO {

	private String password;

	private String username;

	private String bio;

	private String status;


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBio() { return bio;}

	public void setBio(String bio) { this.bio = bio; }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
