package ch.uzh.ifi.hase.soprafs26.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_stats")
public class UserStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private int totalSessions;

    @Column(nullable = false)
    private int totalTranscripts;

    @Column(nullable = false)
    private int totalNotes;

    @Column(nullable = false)
    private int totalFriends;

    @Column(nullable = false)
    private LocalDateTime computedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
