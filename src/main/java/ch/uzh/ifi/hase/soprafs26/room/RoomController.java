package ch.uzh.ifi.hase.soprafs26.room;

import ch.uzh.ifi.hase.soprafs26.entity.User;

import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs26.rest.dto.RoomInviteDTO;

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

    @PostMapping("/rooms/private")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Room createPrivateRoom(@RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        return roomService.createPrivateRoom(userToken.getId());
    }

    @PostMapping("/rooms/{id}/invite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inviteUser(@PathVariable Long id, @RequestBody RoomInviteDTO inviteDTO, @RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");}
        Room room = roomService.getRoomById(Long.toString(id));
        if (room == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");}
        if (!room.isPrivate()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not private");}
        if (!userToken.getId().equals(room.getCreatorId())) {throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can invite users");}
        User invitedUser = UserRepository.findByUsername(inviteDTO.getUsername());
        if (invitedUser == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + inviteDTO.getUsername());}

        roomService.inviteUser(id, invitedUser.getId());
    }

    @GetMapping("/users/{id}/invites")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Room> getInvites(@PathVariable Long id, @RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");}
        if (!userToken.getId().equals(id)) {throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only see your own invites");}
        return roomService.getInvitesForUser(id);
    }

    @PutMapping("/rooms/{id}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Room joinRoom(@PathVariable Long id, @RequestHeader("token") String token) {
        User userToken = UserRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }

        Room room = roomService.getRoomById(Long.toString(id));

        if (room.isPrivate()) {
            boolean isCreator = userToken.getId().equals(room.getCreatorId());
            boolean isInvited = userToken.getId().equals(room.getInvitedUserId());
            if (!isCreator && !isInvited) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not invited to this room");
            }
        }

        if(room.getRoomStatus().equals(RoomStatus.EMPTY)){
            room.setRoomStatus(RoomStatus.JOINABLE);
            room.setCallerID(userToken.getId());
            return room;
        }
        if(room.getRoomStatus().equals(RoomStatus.JOINABLE)){
            room.setRoomStatus(RoomStatus.FULL);
            if(userToken.getId() == room.getCallerID()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User cannot be caller and callee");
            }
            room.setCalleeID(userToken.getId());
            return room;
        }
        if(room.getRoomStatus().equals(RoomStatus.FULL)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is Full");
        }
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
        if(room.getRoomStatus().equals(RoomStatus.JOINABLE)){
            room.setRoomStatus(RoomStatus.EMPTY);
            if (userToken.getId().equals(room.getCallerID())) {
                room.setCallerID(null);
            }
            if (userToken.getId().equals(room.getCalleeID())){
                room.setCalleeID(null);
            }
            room.setBaseTranscript("");
            room.setBaseNote("");
        }
        if(room.getRoomStatus().equals(RoomStatus.FULL)){
            room.setRoomStatus(RoomStatus.JOINABLE);
            if (userToken.getId().equals(room.getCallerID())){
                room.setCallerID(null);
            }
            if (userToken.getId().equals(room.getCalleeID())){
                room.setCalleeID(null);
            }
            room.setBaseTranscript("");
            room.setBaseNote("");
        }

        if (room.isPrivate() && room.getRoomStatus().equals(RoomStatus.EMPTY)) {
            roomService.deleteRoom(id);
            return null;
        }

        return room;
    }
}
