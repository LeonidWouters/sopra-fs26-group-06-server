package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;


public class UserLoginDTO {

    private String password;

    private String username;

    private UserStatus status;

    private long id;

    private String token;

    private String bio;

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

    public void setStatus(UserStatus status) {this.status = status;}

    public UserStatus getStatus() {return UserStatus.ONLINE;}

    public void setId(long id) {this.id = id;}

    public long getId() {return id;}

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
    public String getBio() {return bio;}
    public void setBio(String bio) {this.bio = bio;}

}
