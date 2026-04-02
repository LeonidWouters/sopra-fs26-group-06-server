package ch.uzh.ifi.hase.soprafs26.sockets;

import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


//This class represents a WebSocket session for one specific room. It manages the participants
//of the session, their WebSocket connections, and the session's status (empty, joinable, full). It also tracks
//the last activity time to allow for session cleanup after inactivity. The class provides methods to add and remove participants,
//check if a user is part of the session, and retrieve the peer user's information.
// The session is designed to support a maximum of two participants.

public class Session {

	public enum Status {
		EMPTY,
		JOINABLE,
		FULL
	}
    //Set the maximum number of participants to 2
	private static final int MAX_PARTICIPANTS = 2;

	private final UUID id;
	private final Long roomId;
	private final LocalDateTime createdAt;

	private volatile LocalDateTime lastActivityAt;
	private volatile Status status;

	private Long callerId;
	private Long calleeId;

    // Tracks active participants in the session: userId -> live WebSocketSession
	private final ConcurrentHashMap<Long, WebSocketSession> participants;

    // Constructor for the session, requires a valid roomId.
	public Session(Long roomId) {
        // Reject null or non-positive roomId's
		this.roomId = Objects.requireNonNull(roomId, "roomId must not be null");
		if (this.roomId <= 0L) {
			throw new IllegalArgumentException("roomId must be positive");
		}
		this.id = UUID.randomUUID();
		this.createdAt = LocalDateTime.now();
		this.lastActivityAt = this.createdAt;
		this.status = Status.EMPTY;
		this.participants = new ConcurrentHashMap<>();
	}

    //Getters
	public UUID getId() {
		return id;
	}

	public Long getRoomId() {
		return roomId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getLastActivityAt() {
		return lastActivityAt;
	}

	public Status getStatus() {
		return status;
	}

	public Long getCallerId() {
		return callerId;
	}

	public Long getCalleeId() {
		return calleeId;
	}

    // Adds a participant to the session. Returns true if successful, 
    // false if the session is full or if inputs are invalid.
	public synchronized boolean addParticipant(Long userId, WebSocketSession socketSession) {
		if (userId == null || socketSession == null) {
			return false;
		}
		if (!participants.containsKey(userId) && participants.size() >= MAX_PARTICIPANTS) {
			return false;
		}

		participants.put(userId, socketSession);

		if (callerId == null || callerId.equals(userId)) {
			callerId = userId;
		}
		else if (calleeId == null || calleeId.equals(userId)) {
			calleeId = userId;
		}

		touchAndRecomputeStatus();
		return true;
	}

    // Removes a participant from the session. 
    // If the participant is the caller or callee, it updates those references accordingly.
	public synchronized void removeParticipant(Long userId) {
		if (userId == null) {
			return;
		}
		participants.remove(userId);

		if (userId.equals(callerId)) {
			callerId = null;
		}
		if (userId.equals(calleeId)) {
			calleeId = null;
		}

		if (callerId == null && calleeId != null) {
			callerId = calleeId;
			calleeId = null;
		}

		touchAndRecomputeStatus();
	}

	public boolean containsUser(Long userId) {
		return userId != null && participants.containsKey(userId);
	}

	public Optional<WebSocketSession> getSocket(Long userId) {
		return Optional.ofNullable(participants.get(userId));
	}

	public Optional<Long> getPeerUserId(Long userId) {
		if (userId == null || participants.size() < 2) {
			return Optional.empty();
		}
		return participants.keySet().stream().filter(id -> !id.equals(userId)).findFirst();
	}

	public Optional<WebSocketSession> getPeerSocket(Long userId) {
		return getPeerUserId(userId).flatMap(this::getSocket);
	}

	public Map<Long, WebSocketSession> getParticipantsSnapshot() {
		return Map.copyOf(participants);
	}

	public boolean isEmpty() {
		return participants.isEmpty();
	}

	private void touchAndRecomputeStatus() {
		this.lastActivityAt = LocalDateTime.now();
		int participantsCount = participants.size();
		if (participantsCount == 0) {
			this.status = Status.EMPTY;
		}
		else if (participantsCount == 1) {
			this.status = Status.JOINABLE;
		}
		else {
			this.status = Status.FULL;
		}
	}
    

}
