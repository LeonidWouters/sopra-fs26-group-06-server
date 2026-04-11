package ch.uzh.ifi.hase.soprafs26.sockets;


import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


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
        var urlParameter = org.springframework.web.util.UriComponentsBuilder //use spring tool to parse url (no split necessary)
                .fromUri(session.getUri())
                .build()
                .getQueryParams();

        String token = urlParameter.getFirst("token");
        String roomId = urlParameter.getFirst("roomId");

        if (token == null || roomId == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        User user = userRepository.findByToken(token); //verify user
        if (user == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        try { //join
            Long roomnumber = Long.parseLong(roomId);
            Session raum = sessionManager.createRoom(roomnumber);
            raum.addParticipant(user.getId(), session);

            session.sendMessage(new TextMessage("connected"));
        }
        catch (NumberFormatException exception) {
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Session raum = sessionManager.findRoomByConnection(session);
        if (raum == null) {
            return;
        }

        var urlParameter = org.springframework.web.util.UriComponentsBuilder //same as in line 24
                .fromUri(session.getUri())
                .build()
                .getQueryParams();


        String token = urlParameter.getFirst("token");

        if (token == null) {
            return;
        }

        User sender = userRepository.findByToken(token);

        if (sender == null) {
            return;
        }

        WebSocketSession partnerSession = raum.getPeerSocket(sender.getId()).orElse(null);
        if (partnerSession != null) {
            if (partnerSession.isOpen()) {
                partnerSession.sendMessage(message);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Session raum = sessionManager.findRoomByConnection(session);

        if (raum == null) {
            return;
        }

        var urlParameter = org.springframework.web.util.UriComponentsBuilder //same as line 23
                .fromUri(session.getUri())
                .build()
                .getQueryParams();

        String token = urlParameter.getFirst("token");

        if (token == null) {
            return;
        }
        User user = userRepository.findByToken(token);

        if (user == null) {
            return;
        }
        raum.removeParticipant(user.getId());
        sessionManager.removeEmptyRoom(raum.getRoomId());
    }
}