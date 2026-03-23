package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.DisabilityStatus;

public class UserPutProfileDTO {

    private String username;

    private DisabilityStatus disabilityStatus;

    private String bio;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DisabilityStatus getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(DisabilityStatus disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
