package ch.uzh.ifi.hase.soprafs26.sockets;

import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;
import ch.uzh.ifi.hase.soprafs26.service.ChatService; // Sauberer Import
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
class WebSocketsConfig implements WebSocketConfigurer {

    private final SocketsHandler socketsHandler;

    public WebSocketsConfig(UserRepository userRepository, SessionManager sessionManager, RoomService roomService, ChatService chatService) {
        this.socketsHandler = new SocketsHandler(userRepository, sessionManager, roomService, chatService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this.socketsHandler, "/ws/SocketsHandler")
                .setAllowedOrigins("*");
    }
}