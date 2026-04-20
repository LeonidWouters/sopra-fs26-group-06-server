package ch.uzh.ifi.hase.soprafs26.sockets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SessionManagerTest {

    private SessionManager sessionManager;
    private WebSocketSession mockWebSocketSession1;
    private WebSocketSession mockWebSocketSession2;
    private WebSocketSession mockWebSocketSession3;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
        mockWebSocketSession1 = mock(WebSocketSession.class);
        mockWebSocketSession2 = mock(WebSocketSession.class);
        mockWebSocketSession3 = mock(WebSocketSession.class);
    }

    @Test
    void getOrCreateSession_validRoomId_success() {
        Long roomId = 1L;
        Session session = sessionManager.getOrCreateSession(roomId);
        
        assertNotNull(session);
        assertEquals(roomId, session.getRoomId());
        
        // Ensure returning the same session
        Session sameSession = sessionManager.getOrCreateSession(roomId);
        assertEquals(session, sameSession);
    }

    @Test
    void getOrCreateSession_invalidRoomId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sessionManager.getOrCreateSession(null));
        assertThrows(IllegalArgumentException.class, () -> sessionManager.getOrCreateSession(-1L));
    }

    @Test
    void joinRoom_success() {
        Long roomId = 1L;
        Long userId = 10L;
        
        Session session = sessionManager.joinRoom(roomId, userId, mockWebSocketSession1);
        
        assertNotNull(session);
        assertTrue(session.containsUser(userId));
    }

    @Test
    void joinRoom_invalidInput_throwsException() {
        Long roomId = 1L;
        Long userId = 10L;
        
        // Invalid RoomId
        assertThrows(IllegalArgumentException.class, () -> sessionManager.joinRoom(null, userId, mockWebSocketSession1));
        
        // Invalid UserId
        assertThrows(IllegalArgumentException.class, () -> sessionManager.joinRoom(roomId, null, mockWebSocketSession1));
    }

    @Test
    void joinRoom_roomFull_throwsException() {
        Long roomId = 1L;
        
        sessionManager.joinRoom(roomId, 10L, mockWebSocketSession1);
        sessionManager.joinRoom(roomId, 20L, mockWebSocketSession2);
        
        assertThrows(IllegalStateException.class, () -> sessionManager.joinRoom(roomId, 30L, mockWebSocketSession3));
    }

    @Test
    void leaveRoom_success() {
        Long roomId = 1L;
        Long userId1 = 10L;
        Long userId2 = 20L;
        
        sessionManager.joinRoom(roomId, userId1, mockWebSocketSession1);
        sessionManager.joinRoom(roomId, userId2, mockWebSocketSession2);
        
        Optional<Session> remainingSession = sessionManager.leaveRoom(roomId, userId1);
        
        assertTrue(remainingSession.isPresent());
        assertFalse(remainingSession.get().containsUser(userId1));
        assertTrue(remainingSession.get().containsUser(userId2));
    }

    @Test
    void leaveRoom_lastUser_removesSession() {
        Long roomId = 1L;
        Long userId = 10L;
        
        sessionManager.joinRoom(roomId, userId, mockWebSocketSession1);
        
        Optional<Session> remainingSession = sessionManager.leaveRoom(roomId, userId);
        
        assertFalse(remainingSession.isPresent());
        assertTrue(sessionManager.findByRoomId(roomId).isEmpty());
    }

    @Test
    void leaveRoom_nonExistentSession_returnsEmpty() {
        Optional<Session> result = sessionManager.leaveRoom(1L, 10L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByRoomId_success() {
        Long roomId = 1L;
        sessionManager.getOrCreateSession(roomId);
        
        Optional<Session> foundSession = sessionManager.findByRoomId(roomId);
        assertTrue(foundSession.isPresent());
        assertEquals(roomId, foundSession.get().getRoomId());
    }

    @Test
    void findByUserId_success() {
        Long roomId = 100L;
        Long userId = 10L;
        sessionManager.joinRoom(roomId, userId, mockWebSocketSession1);
        
        Optional<Session> foundSession = sessionManager.findByUserId(userId);
        assertTrue(foundSession.isPresent());
        assertEquals(roomId, foundSession.get().getRoomId());
    }
}
