package ch.uzh.ifi.hase.soprafs26.sockets;


import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



public class SocketsHandler extends TextWebSocketHandler {
    private final UserRepository userRepository;

    public SocketsHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
       String query = session.getUri().getQuery();//get query param, separate check to avoid null pointer

       String token = null;
       if(query != null && query.contains("token=")) {
           token = query.split("token=")[1];
       }

       if(token == null | userRepository.findByToken(token) == null) {
           session.close(CloseStatus.POLICY_VIOLATION);//close session before first message is sent if user is not authenticated
           return;
       }
       session.sendMessage(new TextMessage("connected to" + session.getLocalAddress()));

    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        System.out.println("Received message: " + msg);
        System.out.println("From: " + session.getRemoteAddress());
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection closed: " + session.getRemoteAddress() + " with status " + status);
        System.out.println("CloseStatus: " + status);
    }
}
