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

        return roomService.getRoomById(Long.toString(id));
    }
}
