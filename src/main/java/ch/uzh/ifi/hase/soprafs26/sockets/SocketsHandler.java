package ch.uzh.ifi.hase.soprafs26.sockets;


import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Optional;


public class SocketsHandler extends TextWebSocketHandler {
    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_ROOM_ID = "roomId";
    private static final String TYPE_JOIN = "join";
    private static final String TYPE_LEAVE = "leave";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public SocketsHandler(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
       String token = extractToken(session);
       User user = token == null ? null : userRepository.findByToken(token);

       if (user == null) {
           session.close(CloseStatus.POLICY_VIOLATION);
           return;
       }

       session.getAttributes().put(ATTR_USER_ID, user.getId());
       sendJson(session, Map.of("type", "connected", "userId", user.getId()));

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = getUserId(session);
        if (userId == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        JsonNode payload = OBJECT_MAPPER.readTree(message.getPayload());
        String type = payload.hasNonNull("type") ? payload.get("type").asText() : null;

        if (type == null && payload.has("id")) {
            handleJoin(session, payload, userId);
            return;
        }

        if (TYPE_JOIN.equals(type)) {
            handleJoin(session, payload, userId);
            return;
        }

        if (TYPE_LEAVE.equals(type)) {
            handleLeave(session, userId, true);
            return;
        }

        relayToPeer(session, payload, userId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            handleLeave(session, userId, false);
        }
        System.out.println("Connection closed: " + session.getRemoteAddress() + " with status " + status);
        System.out.println("CloseStatus: " + status);
    }

    private void handleJoin(WebSocketSession session, JsonNode payload, Long userId) throws Exception {
        Long roomId = extractRoomId(payload);
        if (roomId == null) {
            sendJson(session, Map.of("type", "error", "message", "roomId missing in join message"));
            return;
        }

        Session wsSession;
        try {
            wsSession = sessionManager.joinRoom(roomId, userId, session);
        }
        catch (IllegalStateException ex) {
            sendJson(session, Map.of("type", "error", "message", ex.getMessage()));
            return;
        }

        session.getAttributes().put(ATTR_ROOM_ID, roomId);

        ObjectNode joinedPayload = OBJECT_MAPPER.createObjectNode();
        joinedPayload.put("type", "joined");
        joinedPayload.put("roomId", roomId);
        joinedPayload.put("sessionId", wsSession.getId().toString());
        joinedPayload.put("status", wsSession.getStatus().name());
        if (wsSession.getCallerId() != null) {
            joinedPayload.put("callerId", wsSession.getCallerId());
        }
        else {
            joinedPayload.putNull("callerId");
        }
        if (wsSession.getCalleeId() != null) {
            joinedPayload.put("calleeId", wsSession.getCalleeId());
        }
        else {
            joinedPayload.putNull("calleeId");
        }
        sendJson(session, joinedPayload);

        wsSession.getPeerSocket(userId).ifPresent(peer -> {
            try {
                sendJson(peer, Map.of("type", "peer-joined", "roomId", roomId, "userId", userId));
            }
            catch (Exception ignored) {
                // Best-effort peer notifications.
            }
        });
    }

    private void handleLeave(WebSocketSession session, Long userId, boolean notifyCurrent) throws Exception {
        Long roomId = getRoomId(session).orElse(null);
        if (roomId == null) {
            Optional<Session> sessionOpt = sessionManager.findByUserId(userId);
            if (sessionOpt.isEmpty()) {
                return;
            }
            roomId = sessionOpt.get().getRoomId();
        }

        Optional<Session> remainingSession = sessionManager.leaveRoom(roomId, userId);

        session.getAttributes().remove(ATTR_ROOM_ID);

        if (notifyCurrent) {
            sendJson(session, Map.of("type", "left", "roomId", roomId));
        }

        if (remainingSession.isPresent()) {
            remainingSession.get().getPeerSocket(userId).ifPresent(peer -> {
                try {
                    sendJson(peer, Map.of("type", "peer-left", "roomId", roomId, "userId", userId));
                }
                catch (Exception ignored) {
                    // Best-effort peer notifications.
                }
            });
        }
    }

    private void relayToPeer(WebSocketSession session, JsonNode payload, Long userId) throws Exception {
        Long roomId = getRoomId(session).orElse(null);
        if (roomId == null) {
            sendJson(session, Map.of("type", "error", "message", "Join a room before sending messages"));
            return;
        }

        Optional<Session> sessionOpt = sessionManager.findByRoomId(roomId);
        if (sessionOpt.isEmpty() || !sessionOpt.get().containsUser(userId)) {
            sendJson(session, Map.of("type", "error", "message", "Session not found for this user"));
            return;
        }

        Optional<WebSocketSession> peerOpt = sessionOpt.get().getPeerSocket(userId);
        if (peerOpt.isEmpty()) {
            sendJson(session, Map.of("type", "info", "message", "Waiting for peer to join"));
            return;
        }

        ObjectNode relayPayload = payload.deepCopy();
        relayPayload.put("fromUserId", userId);
        relayPayload.put("roomId", roomId);
        sendJson(peerOpt.get(), relayPayload);
    }

    private String extractToken(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return null;
        }
        String query = session.getUri().getQuery();
        for (String param : query.split("&")) {
            String[] parts = param.split("=", 2);
            if (parts.length == 2 && "token".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }

    private Long extractRoomId(JsonNode payload) {
        JsonNode roomIdNode = payload.hasNonNull("roomId") ? payload.get("roomId") : payload.get("id");
        if (roomIdNode == null) {
            return null;
        }
        if (roomIdNode.isNumber()) {
            return roomIdNode.asLong();
        }
        if (roomIdNode.isTextual()) {
            try {
                return Long.parseLong(roomIdNode.asText());
            }
            catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private Long getUserId(WebSocketSession session) {
        Object value = session.getAttributes().get(ATTR_USER_ID);
        return value instanceof Long ? (Long) value : null;
    }

    private Optional<Long> getRoomId(WebSocketSession session) {
        Object value = session.getAttributes().get(ATTR_ROOM_ID);
        if (value instanceof Long) {
            return Optional.of((Long) value);
        }
        return Optional.empty();
    }

    private void sendJson(WebSocketSession session, Object payload) throws Exception {
        if (!session.isOpen()) {
            return;
        }
        session.sendMessage(new TextMessage(OBJECT_MAPPER.writeValueAsString(payload)));
    }
}
