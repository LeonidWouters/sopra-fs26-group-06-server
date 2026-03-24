package ch.uzh.ifi.hase.soprafs26.room;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    public final int NUMBER_OF_ROOMS = 6;

    @PostConstruct
    public void initRooms() {
        for (int i = 1; i <= NUMBER_OF_ROOMS; i++) {
            Room room = Room.createRoom(Long.valueOf(i),"room" + String.valueOf(i),"Some example Text");
            rooms.put(String.valueOf(i), room);
        }
    }
    public List<Room> getAllRooms() {
        return List.copyOf(rooms.values());
    }

    public Room getRoomById(String id) {
        return rooms.get(id);
    }
}

