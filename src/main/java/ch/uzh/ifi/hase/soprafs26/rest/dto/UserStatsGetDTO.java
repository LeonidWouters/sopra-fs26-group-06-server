package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.time.LocalDateTime;

public class UserStatsGetDTO {

    private Long userId;
    private int totalSessions;
    private int totalTranscripts;
    private int totalNotes;
    private int totalFriends;
    private LocalDateTime computedAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public int getTotalTranscripts() { return totalTranscripts; }
    public void setTotalTranscripts(int totalTranscripts) { this.totalTranscripts = totalTranscripts; }

    public int getTotalNotes() { return totalNotes; }
    public void setTotalNotes(int totalNotes) { this.totalNotes = totalNotes; }

    public int getTotalFriends() { return totalFriends; }
    public void setTotalFriends(int totalFriends) { this.totalFriends = totalFriends; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}
