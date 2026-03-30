package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.util.UUID;

public class TranscriptPostDTO {

    private String content;
    private UUID sessionId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }
}
