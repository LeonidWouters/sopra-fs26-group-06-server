package ch.uzh.ifi.hase.soprafs26.room;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.temporal.ChronoUnit;

@Service
public class RoomService {
    @Autowired
    private UserRepository userRepository;

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    public final int NUMBER_OF_ROOMS = 6;
    private long nextPrivateRoomId = 1000;

    @PostConstruct
    public void initRooms() {
        Room room1 = Room.createRoom(1L, "Main Lobby", "Welcome to the main lobby! Hang out and chat.");
        rooms.put("1", room1);

        Room room2 = Room.createRoom(2L, "Gaming Lounge", "Looking for a group? Discuss favorite games here.");
        rooms.put("2", room2);

        Room room3 = Room.createRoom(3L, "Study Room", "Quiet place for studying and sharing notes.");
        rooms.put("3", room3);

        Room room4 = Room.createRoom(4L, "Tech Talk", "Discuss programming and the latest tech news.");
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

    public void updateHeartbeat(String roomId, Long userId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            if (userId.equals(room.getCallerID())) {
                room.setCallerLastHeartbeat(LocalDateTime.now());
            } else if (userId.equals(room.getCalleeID())) {
                room.setCalleeLastHeartbeat(LocalDateTime.now());
            }
        }
    }

    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void cleanupInactiveUsers() {
        LocalDateTime now = LocalDateTime.now();
        for (Room room : rooms.values()) {
            if (room.getCallerID() != null && room.getCallerLastHeartbeat() != null) {
                if (ChronoUnit.SECONDS.between(room.getCallerLastHeartbeat(), now) > 60) {
                    removeUserFromRoom(room, room.getCallerID());
                }
            }
            if (room.getCalleeID() != null && room.getCalleeLastHeartbeat() != null) {
                if (ChronoUnit.SECONDS.between(room.getCalleeLastHeartbeat(), now) > 60) {
                    removeUserFromRoom(room, room.getCalleeID());
                }
            }
        }
    }

    private void removeUserFromRoom(Room room, Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            boolean isCaller = userId.equals(room.getCallerID());
            boolean isCallee = userId.equals(room.getCalleeID());

            if (room.getRoomStatus().equals(RoomStatus.JOINABLE)) {
                room.setRoomStatus(RoomStatus.EMPTY);
                if (isCaller) room.setCallerID(null);
                if (isCallee) room.setCalleeID(null);
                user.setRoomId(null);
                userRepository.save(user);
                room.setBaseTranscript("");
                room.setBaseNote("");
            } else if (room.getRoomStatus().equals(RoomStatus.FULL)) {
                room.setRoomStatus(RoomStatus.JOINABLE);
                if (isCaller) room.setCallerID(null);
                if (isCallee) room.setCalleeID(null);
                user.setRoomId(null);
                userRepository.save(user);
                room.setBaseTranscript("");
                room.setBaseNote("");
            }
            if (room.getRoomStatus() == RoomStatus.EMPTY && room.getIsPrivate()) {
                removeRoom(String.valueOf(room.getId()));
            }
        });
    }
}

