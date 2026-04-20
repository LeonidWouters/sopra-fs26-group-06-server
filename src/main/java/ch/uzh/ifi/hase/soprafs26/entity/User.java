package ch.uzh.ifi.hase.soprafs26.entity;

import ch.uzh.ifi.hase.soprafs26.constant.DisabilityStatus;
import jakarta.persistence.*;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private DisabilityStatus disabilityStatus;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @ElementCollection
    @CollectionTable(name = "user_sessions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "session_id")
    private List<UUID> sessions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_pending_requests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "requester_id")
    private List<Long> pendingFriendRequests = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "friend_id")
    private List<Long> friends = new ArrayList<>();

    @Column
    private Long roomId;

    @PrePersist
    protected void onCreate() {
        setCreationDate(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public DisabilityStatus getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(DisabilityStatus disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public List<UUID> getSessions() {
        return sessions;
    }

    public void setSessions(List<UUID> sessions) {
        this.sessions = sessions;
    }

    public List<Long> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public void setPendingFriendRequests(List<Long> pendingFriendRequests) {
        this.pendingFriendRequests = pendingFriendRequests;
    }

    public List<Long> getFriends() {
        return friends;
    }

    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
