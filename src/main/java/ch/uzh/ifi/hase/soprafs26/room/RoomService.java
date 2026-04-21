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
        Room room1 = Room.createRoom(1L, "Main Lobby", "Welcome to the main lobby! Hang out and chat.");
        rooms.put("1", room1);

        Room room2 = Room.createRoom(2L, "Gaming Lounge", "Looking for a group? Discuss your favorite games here.");
        rooms.put("2", room2);

        Room room3 = Room.createRoom(3L, "Study Room", "Quiet place for studying and sharing notes.");
        rooms.put("3", room3);

        Room room4 = Room.createRoom(4L, "Tech Talk", "Discuss programming, hardware, and the latest tech news.");
        rooms.put("4", room4);

        Room room5 = Room.createRoom(5L, "Movie Club", "Watch parties and movie discussions.");
        rooms.put("5", room5);

        Room room6 = Room.createRoom(6L, "Random Chat", "Talk about whatever is on your mind.");
        rooms.put("6", room6);
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
        if (!room.getIsPrivate()) {
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

