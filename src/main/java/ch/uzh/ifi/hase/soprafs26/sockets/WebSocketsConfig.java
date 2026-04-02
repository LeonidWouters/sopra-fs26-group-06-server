package ch.uzh.ifi.hase.soprafs26.sockets;

import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
class WebSocketsConfig implements WebSocketConfigurer {

	private final WebSocketHandler socketsHandler;

	WebSocketsConfig(WebSocketHandler socketsHandler) {
		this.socketsHandler = socketsHandler;
	}

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketsHandler, "/ws/SocketsHandler")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler socketsHandler(UserRepository userRepository, SessionManager sessionManager) {
        return new SocketsHandler(userRepository, sessionManager);
    }

}
