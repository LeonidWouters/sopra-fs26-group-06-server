package ch.uzh.ifi.hase.soprafs26.room;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public Room createPrivateRoom(Long creatorId) {
        long newId = nextPrivateRoomId++;
        Room room = Room.createPrivateRoom(newId, creatorId);
        rooms.put(String.valueOf(newId), room);
        return room;
    }
    public void inviteUser(Long roomId, Long invitedUserId) {
        Room room = rooms.get(String.valueOf(roomId));
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        room.setInvitedUserId(invitedUserId);
    }
    public List<Room> getInvitesForUser(Long userId) {
        List<Room> invites = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.isPrivate()
                    && userId.equals(room.getInvitedUserId())
                    && room.getRoomStatus() == RoomStatus.EMPTY) {
                invites.add(room);
            }
        }
        return invites;
    }
    public void deleteRoom(Long roomId) {
        rooms.remove(String.valueOf(roomId));
    }

}

