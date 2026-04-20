package ch.uzh.ifi.hase.soprafs26.sockets;

import ch.uzh.ifi.hase.soprafs26.room.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SessionTest {

    private Session session;
    private WebSocketSession mockSocket1;
    private WebSocketSession mockSocket2;
    private WebSocketSession mockSocket3;

    @BeforeEach
    void setUp() {
        session = new Session(1L);
        mockSocket1 = mock(WebSocketSession.class);
        mockSocket2 = mock(WebSocketSession.class);
        mockSocket3 = mock(WebSocketSession.class);
    }

    @Test
    void constructor_validRoomId_createsSession() {
        assertNotNull(session.getId());
        assertEquals(1L, session.getRoomId());
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getLastActivityAt());
        assertEquals(RoomStatus.EMPTY, session.getStatus());
        assertTrue(session.isEmpty());
        assertNull(session.getCallerId());
        assertNull(session.getCalleeId());
    }

    @Test
    void constructor_invalidRoomId_throwsException() {
        assertThrows(NullPointerException.class, () -> new Session(null));
        assertThrows(IllegalArgumentException.class, () -> new Session(0L));
        assertThrows(IllegalArgumentException.class, () -> new Session(-1L));
    }

    @Test
    void addParticipant_success() {
        Long userId = 10L;
        boolean result = session.addParticipant(userId, mockSocket1);
        
        assertTrue(result);
        assertTrue(session.containsUser(userId));
        assertEquals(userId, session.getCallerId());
        assertNull(session.getCalleeId());
        assertEquals(RoomStatus.JOINABLE, session.getStatus());
    }

    @Test
    void addParticipant_twoUsers_success() {
        Long callerId = 10L;
        Long calleeId = 20L;
        
        session.addParticipant(callerId, mockSocket1);
        session.addParticipant(calleeId, mockSocket2);
        
        assertTrue(session.containsUser(callerId));
        assertTrue(session.containsUser(calleeId));
        assertEquals(callerId, session.getCallerId());
        assertEquals(calleeId, session.getCalleeId());
        assertEquals(RoomStatus.FULL, session.getStatus());
    }

    @Test
    void addParticipant_fullRoom_returnsFalse() {
        session.addParticipant(10L, mockSocket1);
        session.addParticipant(20L, mockSocket2);
        
        boolean result = session.addParticipant(30L, mockSocket3);
        
        assertFalse(result);
        assertEquals(RoomStatus.FULL, session.getStatus());
    }

    @Test
    void addParticipant_invalidInput_returnsFalse() {
        assertFalse(session.addParticipant(null, mockSocket1));
        assertFalse(session.addParticipant(10L, null));
    }

    @Test
    void removeParticipant_success() {
        Long callerId = 10L;
        Long calleeId = 20L;
        
        session.addParticipant(callerId, mockSocket1);
        session.addParticipant(calleeId, mockSocket2);
        
        session.removeParticipant(callerId);
        
        assertFalse(session.containsUser(callerId));
        assertTrue(session.containsUser(calleeId));
        assertEquals(calleeId, session.getCallerId()); // Callee becomes caller
        assertNull(session.getCalleeId());
        assertEquals(RoomStatus.JOINABLE, session.getStatus());
    }

    @Test
    void removeParticipant_lastUser_makesRoomEmpty() {
        Long userId = 10L;
        session.addParticipant(userId, mockSocket1);
        
        session.removeParticipant(userId);
        
        assertTrue(session.isEmpty());
        assertNull(session.getCallerId());
        assertEquals(RoomStatus.EMPTY, session.getStatus());
    }

    @Test
    void nullUserIdQueries_returnEmpty() {
        assertFalse(session.containsUser(null));
        assertThrows(NullPointerException.class, () -> session.getSocket(null));
        assertTrue(session.getPeerUserId(null).isEmpty());
        assertTrue(session.getPeerSocket(null).isEmpty());
    }

    @Test
    void getPeerUserId_andSocket_success() {
        Long user1 = 10L;
        Long user2 = 20L;
        
        session.addParticipant(user1, mockSocket1);
        session.addParticipant(user2, mockSocket2);
        
        Optional<Long> peerOfUser1 = session.getPeerUserId(user1);
        Optional<Long> peerOfUser2 = session.getPeerUserId(user2);
        
        assertTrue(peerOfUser1.isPresent());
        assertEquals(user2, peerOfUser1.get());
        
        assertTrue(peerOfUser2.isPresent());
        assertEquals(user1, peerOfUser2.get());
        
        Optional<WebSocketSession> peerSocketOfUser1 = session.getPeerSocket(user1);
        assertTrue(peerSocketOfUser1.isPresent());
        assertEquals(mockSocket2, peerSocketOfUser1.get());
    }
}
