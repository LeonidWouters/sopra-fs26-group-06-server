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
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private UserRepository userRepository;

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

    @Test
    public void updateHeartbeat_updatesCallerHeartbeat() {
        Room room = roomService.getRoomById("1");
        room.setCallerID(5L);
        
        roomService.updateHeartbeat("1", 5L);
        
        assertNotNull(room.getCallerLastHeartbeat());
    }

    @Test
    public void updateHeartbeat_updatesCalleeHeartbeat() {
        Room room = roomService.getRoomById("1");
        room.setCalleeID(10L);
        
        roomService.updateHeartbeat("1", 10L);
        
        assertNotNull(room.getCalleeLastHeartbeat());
    }

    @Test
    public void cleanupInactiveUsers_removesInactiveCaller_keepsActiveCallee() {
        // give room an active callee and an inactive caller
        Room room = roomService.getRoomById("1");
        room.setRoomStatus(RoomStatus.FULL);
        Long callerId = 3L;
        Long calleeId = 4L;
        room.setCallerID(callerId);
        room.setCalleeID(calleeId);
        // Caller heartbeat is 35 seconds ago -> should be cleaned up
        room.setCallerLastHeartbeat(LocalDateTime.now().minusSeconds(35));
        // Callee heartbeat is 10 seconds ago -> should NOT be cleaned up
        room.setCalleeLastHeartbeat(LocalDateTime.now().minusSeconds(10));
        User mockCaller = new User();
        mockCaller.setId(callerId);
        mockCaller.setRoomId(room.getId());
        when(userRepository.findById(callerId)).thenReturn(Optional.of(mockCaller));
        // Call cleanup
        roomService.cleanupInactiveUsers();
        // Verify caller is removed
        assertNull(room.getCallerID());
        assertNull(mockCaller.getRoomId());
        // Verify callee is still in the room
        assertEquals(calleeId, room.getCalleeID());
        assertEquals(RoomStatus.JOINABLE, room.getRoomStatus()); // Changed to JOINABLE because one left
    }

    @Test
    public void inviteUser_throwsExceptionWhenInvitingNonFriend() {
        Long creatorId = 100L;
        Room privateRoom = roomService.createPrivateRoom(creatorId, "Secret Room", "Shhh!");
        User inviter = new User();
        inviter.setId(creatorId);
        inviter.setFriends(List.of(200L)); // friend id is 200
        User invited = new User();
        // NOT a friend
        invited.setId(300L);
        assertThrows(ResponseStatusException.class, () -> {
            roomService.inviteUser(String.valueOf(privateRoom.getId()), inviter, invited);
        }, "Should throw an exception if invited user is not a friend");
    }
}
