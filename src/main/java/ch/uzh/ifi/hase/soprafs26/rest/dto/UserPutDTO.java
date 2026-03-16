package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class UserPutDTO {
    private String password;

    private String username;

    private String disabilityStatus;
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {this.username = username;}

    public String getDisabilityStatus() {
        return disabilityStatus;
    }
    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }
}
