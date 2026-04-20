package ch.uzh.ifi.hase.soprafs26.room;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoomService {
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    public final int NUMBER_OF_ROOMS = 6;
    private long nextPrivateRoomId = 1000;

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

    public Room createPrivateRoom(Long creatorId, String name, String description) {
        long newId = nextPrivateRoomId++;
        Room room = Room.createPrivateRoom(newId, creatorId, name, description);
        rooms.put(String.valueOf(newId), room);
        return room;
    }

    public void inviteUser(String roomId, User inviter, User invited) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        if (!room.isPrivate()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not private");
        }
        if (!inviter.getFriends().contains(invited.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only invite friends");
        }
        room.setInvitedUserId(invited.getId());
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }
}

