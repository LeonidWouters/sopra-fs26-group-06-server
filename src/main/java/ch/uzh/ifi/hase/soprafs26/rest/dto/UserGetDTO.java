package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.DisabilityStatus;
import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;

import java.time.LocalDateTime;

public class UserGetDTO {

	private Long id;
	private String username;
	private String name;
	private UserStatus status;
	private String bio;
	private  LocalDateTime creationDate;
	private DisabilityStatus disabilityStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getBio() {return bio;}

	public void setBio(String bio) {this.bio = bio;}

	public LocalDateTime getCreationDate() {return creationDate;}

	public void setCreationDate(LocalDateTime creationDate) {this.creationDate = creationDate;}

	public DisabilityStatus getDisabilityStatus() {return disabilityStatus;}

	public void setDisabilityStatus(DisabilityStatus disabilityStatus) {this.disabilityStatus = disabilityStatus;}
}
