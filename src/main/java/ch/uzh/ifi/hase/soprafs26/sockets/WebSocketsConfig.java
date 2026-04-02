package ch.uzh.ifi.hase.soprafs26.sockets;

import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
class WebSocketsConfig implements WebSocketConfigurer {

	private final UserRepository userRepository;
	private final SessionManager sessionManager;

	WebSocketsConfig(UserRepository userRepository, SessionManager sessionManager) {
		this.userRepository = userRepository;
		this.sessionManager = sessionManager;
	}

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketsHandler(userRepository, sessionManager), "/ws/SocketsHandler")
                .setAllowedOrigins("*");
    }

}
