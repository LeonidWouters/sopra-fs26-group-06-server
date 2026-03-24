package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class UserPutDTO {
    private String password;

    private String username;

    private String firstname;
    private String lastname;

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

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {this.firstname = firstname;}

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {this.lastname = lastname;}

    public String getDisabilityStatus() {
        return disabilityStatus;
    }
    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }
}
