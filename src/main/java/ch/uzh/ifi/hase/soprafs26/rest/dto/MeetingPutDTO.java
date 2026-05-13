package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.time.LocalDateTime;

public class MeetingPutDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long invitedUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Long getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(Long invitedUser) {
        this.invitedUser = invitedUser;
    }
}
