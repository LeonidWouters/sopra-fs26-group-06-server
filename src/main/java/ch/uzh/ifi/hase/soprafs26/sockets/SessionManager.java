package ch.uzh.ifi.hase.soprafs26.sockets;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {

	private final ConcurrentHashMap<Long, Session> sessionsByRoomId = new ConcurrentHashMap<>();

	public Session getOrCreateSession(Long roomId) {
		validateRoomId(roomId);
		return sessionsByRoomId.computeIfAbsent(roomId, Session::new);
	}

	public synchronized Session joinRoom(Long roomId, Long userId, WebSocketSession socketSession) {
		validateRoomId(roomId);
		validateUserId(userId);
		Session session = getOrCreateSession(roomId);
		boolean added = session.addParticipant(userId, socketSession);
		if (!added) {
			throw new IllegalStateException("Room " + roomId + " is full.");
		}
		return session;
	}

	public synchronized Optional<Session> leaveRoom(Long roomId, Long userId) {
		validateRoomId(roomId);
		validateUserId(userId);

		Session session = sessionsByRoomId.get(roomId);
		if (session == null) {
			return Optional.empty();
		}

		session.removeParticipant(userId);
		if (session.isEmpty()) {
			sessionsByRoomId.remove(roomId, session);
			return Optional.empty();
		}
		return Optional.of(session);
	}

	public Optional<Session> findByRoomId(Long roomId) {
		if (roomId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(sessionsByRoomId.get(roomId));
	}

	public Optional<Session> findByUserId(Long userId) {
		if (userId == null) {
			return Optional.empty();
		}
		return sessionsByRoomId.values().stream().filter(session -> session.containsUser(userId)).findFirst();
	}

	public Map<Long, Session> getSessionsSnapshot() {
		return Map.copyOf(sessionsByRoomId);
	}

	private void validateRoomId(Long roomId) {
		if (roomId == null || roomId <= 0L) {
			throw new IllegalArgumentException("roomId must be positive");
		}
	}

	private void validateUserId(Long userId) {
		if (userId == null || userId <= 0L) {
			throw new IllegalArgumentException("userId must be positive");
		}
	}
}
