package ch.uzh.ifi.hase.soprafs26.room;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class RoomController {

    private final RoomService RoomService;

    RoomController(RoomService RoomService) {
        this.RoomService = RoomService;
    }

    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Room> getRooms(@RequestHeader("token") String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        if(RoomService.getAllRooms() == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Rooms present in database");
        }
        return RoomService.getAllRooms();
    }

    @GetMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Room getRoom(@PathVariable Long id, @RequestHeader("token") String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        return RoomService.getRoomById(Long.toString(id));
    }
}
