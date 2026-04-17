package ch.uzh.ifi.hase.soprafs26.sockets;

import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
class WebSocketsConfig implements WebSocketConfigurer {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final RoomService roomService;


    public WebSocketsConfig(UserRepository userRepository, SessionManager sessionManager, RoomService roomService) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
        this.roomService = roomService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketsHandler(userRepository, sessionManager, roomService), "/ws/SocketsHandler")
                .setAllowedOrigins("*");
    }
}
