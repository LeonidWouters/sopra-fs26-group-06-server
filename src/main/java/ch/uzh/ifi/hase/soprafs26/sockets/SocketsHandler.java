package ch.uzh.ifi.hase.soprafs26.sockets;


import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;


public class SocketsHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session.getUri());

        TextMessage greeting = new TextMessage("Connected to " + session.getRemoteAddress());

        session.sendMessage(greeting);

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
