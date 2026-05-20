package ch.uzh.ifi.hase.soprafs26.sockets;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.room.Room;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SocketsHandlerTest {

    private SocketsHandler socketsHandler;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private RoomService roomService;
    private WebSocketSession mockSession;
    private Map<String, Object> sessionAttributes;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        sessionManager = mock(SessionManager.class);
        roomService = mock(RoomService.class);
        socketsHandler = new SocketsHandler(userRepository, sessionManager, roomService);

        mockSession = mock(WebSocketSession.class);
        sessionAttributes = new HashMap<>();
        when(mockSession.getAttributes()).thenReturn(sessionAttributes);
        when(mockSession.isOpen()).thenReturn(true);
    }

    @Test
    void afterConnectionEstablished_validToken_success() throws Exception {
        URI uri = new URI("ws://localhost/ws?token=valid-token");
        when(mockSession.getUri()).thenReturn(uri);

        User mockUser = new User();
        mockUser.setId(10L);
        when(userRepository.findByToken("valid-token")).thenReturn(mockUser);

        socketsHandler.afterConnectionEstablished(mockSession);

        verify(mockSession).sendMessage(any(TextMessage.class));
        assert(sessionAttributes.get("userId").equals(10L));
    }

    @Test
    void afterConnectionEstablished_invalidToken_closesSession() throws Exception {
        URI uri = new URI("ws://localhost/ws?token=invalid-token");
        when(mockSession.getUri()).thenReturn(uri);
        when(userRepository.findByToken("invalid-token")).thenReturn(null);

        socketsHandler.afterConnectionEstablished(mockSession);

        verify(mockSession).close(CloseStatus.POLICY_VIOLATION);
    }

    @Test
    void handleTextMessage_missingUserId_closesSession() throws Exception {
        TextMessage message = new TextMessage("{\"type\": \"join\", \"roomId\": 1}");
        socketsHandler.handleTextMessage(mockSession, message);

        verify(mockSession).close(CloseStatus.POLICY_VIOLATION);
    }

    @Test
    void handleTextMessage_join_success() throws Exception {
        sessionAttributes.put("userId", 10L);
        TextMessage message = new TextMessage("{\"type\": \"join\", \"roomId\": 1}");

        Session mockWsSession = mock(Session.class);
        when(mockWsSession.getId()).thenReturn(java.util.UUID.randomUUID());
        when(mockWsSession.getStatus()).thenReturn(ch.uzh.ifi.hase.soprafs26.room.RoomStatus.JOINABLE);
        when(sessionManager.joinRoom(eq(1L), eq(10L), eq(mockSession))).thenReturn(mockWsSession);

        socketsHandler.handleTextMessage(mockSession, message);

        assert(sessionAttributes.get("roomId").equals(1L));
        verify(mockSession, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void handleTextMessage_leave_success() throws Exception {
        sessionAttributes.put("userId", 10L);
        sessionAttributes.put("roomId", 1L);
        TextMessage message = new TextMessage("{\"type\": \"leave\"}");

        when(sessionManager.leaveRoom(1L, 10L)).thenReturn(java.util.Optional.empty());

        socketsHandler.handleTextMessage(mockSession, message);

        assert(!sessionAttributes.containsKey("roomId"));
        verify(mockSession, times(1)).sendMessage(any(TextMessage.class));
        verify(sessionManager).leaveRoom(1L, 10L);
    }

    @Test
    void handleTextMessage_markdownUpdate_updatesRoom() throws Exception {
        sessionAttributes.put("userId", 10L);
        sessionAttributes.put("roomId", 1L);
        TextMessage message = new TextMessage("{\"type\": \"markdown-update\", \"content\": \"new text\"}");

        Room mockRoom = mock(Room.class);
        when(roomService.getRoomById("1")).thenReturn(mockRoom);
        
        Session mockWsSession = mock(Session.class);
        when(mockWsSession.containsUser(10L)).thenReturn(true);
        when(sessionManager.findByRoomId(1L)).thenReturn(java.util.Optional.of(mockWsSession));
        when(mockWsSession.getPeerSocket(10L)).thenReturn(java.util.Optional.empty());

        socketsHandler.handleTextMessage(mockSession, message);

        verify(roomService).getRoomById("1");
        verify(mockRoom).setBaseNote("new text");
    }

    @Test
    void handleTextMessage_speechToText_isIgnored() throws Exception {
        sessionAttributes.put("userId", 10L);
        sessionAttributes.put("roomId", 1L);
        TextMessage message = new TextMessage("{\"type\": \"speech-to-text\", \"content\": \"hello\"}");

        Room mockRoom = mock(Room.class);
        when(mockRoom.getBaseTranscript()).thenReturn("existing");
        when(roomService.getRoomById("1")).thenReturn(mockRoom);

        Session mockWsSession = mock(Session.class);
        when(mockWsSession.containsUser(10L)).thenReturn(true);
        when(sessionManager.findByRoomId(1L)).thenReturn(java.util.Optional.of(mockWsSession));
        when(mockWsSession.getPeerSocket(10L)).thenReturn(java.util.Optional.empty());

        socketsHandler.handleTextMessage(mockSession, message);

        verify(mockRoom, never()).setBaseTranscript(any());
    }

    @Test
    void afterConnectionClosed_handlesLeave() throws Exception {
        sessionAttributes.put("userId", 10L);
        sessionAttributes.put("roomId", 1L);

        when(sessionManager.leaveRoom(1L, 10L)).thenReturn(java.util.Optional.empty());

        socketsHandler.afterConnectionClosed(mockSession, CloseStatus.NORMAL);

        verify(sessionManager).leaveRoom(1L, 10L);
        assert(!sessionAttributes.containsKey("roomId"));
    }

    @Test
    void handleTextMessage_textMsg_appendsToBaseTranscript() throws Exception {
        sessionAttributes.put("userId", 10L);
        sessionAttributes.put("roomId", 1L);

        TextMessage message = new TextMessage("{\"type\": \"text-msg\", \"content\": {\"message\": \"Hello World\", \"timestamp\": \"10:00\"}}");

        Room mockRoom = mock(Room.class);
        when(mockRoom.getBaseTranscript()).thenReturn("");
        when(roomService.getRoomById("1")).thenReturn(mockRoom);

        User mockUser = new User();
        mockUser.setUsername("test");
        when(userRepository.findById(10L)).thenReturn(java.util.Optional.of(mockUser));

        Session mockWsSession = mock(Session.class);
        when(mockWsSession.containsUser(10L)).thenReturn(true);
        when(sessionManager.findByRoomId(1L)).thenReturn(java.util.Optional.of(mockWsSession));
        when(mockWsSession.getPeerSocket(10L)).thenReturn(java.util.Optional.empty());

        socketsHandler.handleTextMessage(mockSession, message);

        verify(mockRoom, times(1)).setBaseTranscript("10:00, test : Hello World\n\n");
    }

    @Test
    void handleTextMessage_withPeer_relaysMessageToPeerSession() throws Exception {//tests if messages are forwarded correctly to peer
        sessionAttributes.put("userId", 10L);
        sessionAttributes.put("roomId", 1L);
        TextMessage message = new TextMessage("{\"type\": \"random-message\", \"someData\": 123}");

        Room mockRoom = mock(Room.class);
        when(roomService.getRoomById("1")).thenReturn(mockRoom);

        WebSocketSession peerSession = mock(WebSocketSession.class);
        when(peerSession.isOpen()).thenReturn(true);

        Session mockWsSession = mock(Session.class);
        when(mockWsSession.containsUser(10L)).thenReturn(true);
        when(sessionManager.findByRoomId(1L)).thenReturn(java.util.Optional.of(mockWsSession));
        when(mockWsSession.getPeerSocket(10L)).thenReturn(java.util.Optional.of(peerSession));

        socketsHandler.handleTextMessage(mockSession, message);

        verify(peerSession, times(1)).sendMessage(any(TextMessage.class));
    }
}