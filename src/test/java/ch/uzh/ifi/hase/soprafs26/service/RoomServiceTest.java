package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.room.Room;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;

import ch.uzh.ifi.hase.soprafs26.room.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {


    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    public void setup() {
        roomService.initRooms();
    }

    @Test
    public void createRoom_isValid() throws Exception {
        Room room = Room.createRoom(1L, "Main Lobby", "Welcome to the main lobby! Hang out and chat.");
        Room createdRoom = roomService.getRoomById("1");
        assertNotNull(createdRoom);
        assertEquals(room.getId(), createdRoom.getId());
        assertEquals(room.getName(), createdRoom.getName());
        assertEquals(room.getDescription(), createdRoom.getDescription());
        assertEquals(room.getRoomStatus(), createdRoom.getRoomStatus());
        assertEquals(room.getBaseTranscript(), createdRoom.getBaseTranscript());
        assertEquals(room.getBaseNote(), createdRoom.getBaseNote());

    }

    @Test
    public void getAllRooms_isValid() throws Exception {

        List<Room> allRooms = roomService.getAllRooms();

        assertEquals(6,allRooms.size() );


    }

    @Test
    public void getRoomById_isValid() throws Exception {
        Room retrievedRoom = roomService.getRoomById("1");
        assertNotNull(retrievedRoom);
        assertEquals(1L, retrievedRoom.getId());
        assertEquals("Main Lobby", retrievedRoom.getName());
        assertEquals("Welcome to the main lobby! Hang out and chat.", retrievedRoom.getDescription());
        assertEquals(RoomStatus.EMPTY, retrievedRoom.getRoomStatus());
        assertEquals("", retrievedRoom.getBaseTranscript());
        assertEquals("", retrievedRoom.getBaseNote());
    }

}
