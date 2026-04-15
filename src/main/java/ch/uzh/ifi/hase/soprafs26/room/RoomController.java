package ch.uzh.ifi.hase.soprafs26.room;

import ch.uzh.ifi.hase.soprafs26.entity.User;

import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class RoomController {

    private final RoomService roomService;
    private final UserRepository UserRepository;

    RoomController(RoomService RoomService, UserRepository UserRepository) {
        this.roomService = RoomService;
        this.UserRepository = UserRepository;

    }

    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Room> getRooms(@RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        if(roomService.getAllRooms() == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Rooms present in database");
        }
        return roomService.getAllRooms();
    }

    @GetMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Room getRoom(@PathVariable Long id, @RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        Room room = roomService.getRoomById(Long.toString(id));
        if(room == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        return room;
    }
    @PutMapping("/rooms/{id}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Room joinRoom(@PathVariable Long id, @RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }

        Room room =  roomService.getRoomById(Long.toString(id));
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        normalizeRoomParticipants(room);
        recomputeRoomStatus(room);

        if (userToken.getId().equals(room.getCallerID()) || userToken.getId().equals(room.getCalleeID())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already in this room");
        }

        if(room.getRoomStatus().equals(RoomStatus.EMPTY)){
            room.setCallerID(userToken.getId());
        }

        else if(room.getRoomStatus().equals(RoomStatus.JOINABLE)){
            if (room.getCallerID() == null) {
                room.setCallerID(userToken.getId());
            }
            else {
                room.setCalleeID(userToken.getId());
            }
        }

        else if(room.getRoomStatus().equals(RoomStatus.FULL)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is Full");
        }

        recomputeRoomStatus(room);
        userToken.setRoomId(room.getId());
        UserRepository.save(userToken);

        return room;
    }
    @PutMapping("/rooms/{id}/leave")
    @ResponseStatus(HttpStatus.OK)

    @ResponseBody
    public Room leaveRoom(@PathVariable Long id, @RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }

        Room room =  roomService.getRoomById(Long.toString(id));
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        if (userToken.getId().equals(room.getCallerID())) {
            room.setCallerID(null);
        }
        if (userToken.getId().equals(room.getCalleeID())) {
            room.setCalleeID(null);
        }

        normalizeRoomParticipants(room);
        recomputeRoomStatus(room);

        userToken.setRoomId(null);
        UserRepository.save(userToken);

        if(room.getRoomStatus().equals(RoomStatus.EMPTY)){
            room.setBaseTranscript("");
            room.setBaseNote("");
        }

        return room;

    }

    private void normalizeRoomParticipants(Room room) {
        if (room.getCallerID() == null && room.getCalleeID() != null) {
            room.setCallerID(room.getCalleeID());
            room.setCalleeID(null);
        }
    }

    private void recomputeRoomStatus(Room room) {
        boolean hasCaller = room.getCallerID() != null;
        boolean hasCallee = room.getCalleeID() != null;

        if (!hasCaller && !hasCallee) {
            room.setRoomStatus(RoomStatus.EMPTY);
            return;
        }

        if (hasCaller && hasCallee) {
            room.setRoomStatus(RoomStatus.FULL);
            return;
        }

        room.setRoomStatus(RoomStatus.JOINABLE);
    }
}
