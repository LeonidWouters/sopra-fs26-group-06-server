package ch.uzh.ifi.hase.soprafs26.sockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final ConcurrentHashMap<Long, Session> activeRooms = new ConcurrentHashMap<>();

    public Session createRoom(Long roomId) {
        if (!activeRooms.containsKey(roomId)) {
            Session newRoom = new Session(roomId);
            activeRooms.put(roomId, newRoom);
        }
        return activeRooms.get(roomId);
    }

    public void removeEmptyRoom(Long roomId) {
        Session room = activeRooms.get(roomId);

        if (room != null) {
            if (room.isEmpty()) {
                activeRooms.remove(roomId);
            }
        }
    }

    public Session findRoomByConnection(WebSocketSession connection) {
        for (Session room : activeRooms.values()) {
            if (room.getParticipantsSnapshot().containsValue(connection)) {
                return room;
            }
        }

        return null;
    }
}