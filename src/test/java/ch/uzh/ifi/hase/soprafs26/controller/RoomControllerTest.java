package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.room.Room;
import ch.uzh.ifi.hase.soprafs26.room.RoomController;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;
import ch.uzh.ifi.hase.soprafs26.room.RoomStatus;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;

import java.util.List;

import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private UserRepository userRepository; //needed to mock user authentication

    @MockitoBean
    private UserService userService; //needed to mock user authentication

    private User user = new User();

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    @BeforeEach
    public void setup() {//mock user object to return for auth
        user.setUsername("username");
        user.setPassword("password");
        user.setToken("1");
        user.setBio("bio");
        user.setName("name");
        user.setId(1L);
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now());


        for (int i = 1; i <= 6; i++) {
            Room room = Room.createRoom(Long.valueOf(i),"room" + String.valueOf(i),"Some example Text");
            rooms.put(String.valueOf(i), room);
        }
    }

    @Test
    public void givenRooms_whenGetRooms_returnsJson() throws Exception {
        given(roomService.getAllRooms()).willReturn(List.copyOf(rooms.values()));
        given(userRepository.findByToken("1")).willReturn(user);
        MockHttpServletRequestBuilder getRequest = get("/rooms").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");


        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].roomStatus", is("EMPTY")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].roomStatus", is("EMPTY")));
    }

    @Test
    public void noRoom_when_getRooms_notExists() throws Exception {
        given(userRepository.findByToken(Mockito.anyString())).willReturn(user);
        given(roomService.getAllRooms()).willReturn(null);

        MockHttpServletRequestBuilder getRequest = get("/rooms").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    public void Room_when_getById_returnsRoom() throws Exception {
        given(userRepository.findByToken("1")).willReturn(user);
        given(roomService.getRoomById("1")).willReturn(rooms.get("1"));
        Room room = rooms.get("1");
        MockHttpServletRequestBuilder getRequest = get("/rooms/1").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(room.getId().intValue())))
                .andExpect(jsonPath("$.roomStatus", is(room.getRoomStatus().toString())))
                .andExpect(jsonPath("$.name", is(room.getName())))
                .andExpect(jsonPath("$.description", is(room.getDescription())));

    }

    @Test
    public void Room_when_join_returnsUpdatedRoom() throws Exception {
        given(userRepository.findByToken("1")).willReturn(user);
        given(roomService.getRoomById("1")).willReturn(rooms.get("1"));


        MockHttpServletRequestBuilder getRequest = put("/rooms/1/join").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(roomService.getRoomById("1").getId().intValue())))
                .andExpect(jsonPath("$.roomStatus", is(roomService.getRoomById("1").getRoomStatus().toString())))
                .andExpect(jsonPath("$.CallerID", is(roomService.getRoomById("1").getCallerID().intValue())))
                .andExpect(jsonPath("$.name", is(roomService.getRoomById("1").getName())))
                .andExpect(jsonPath("$.description", is(roomService.getRoomById("1").getDescription())));

    }

    @Test
    public void Room_when_join_cannotBeCallerCallee() throws Exception {
        given(userRepository.findByToken("1")).willReturn(user);
        given(roomService.getRoomById("1")).willReturn(rooms.get("1"));
        Room room = rooms.get("1");
        room.setRoomStatus(RoomStatus.JOINABLE);
        room.setCallerID(1L);

        MockHttpServletRequestBuilder getRequest = put("/rooms/1/join").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest).andExpect(status().isConflict());

    }

    @Test
    public void Room_when_leaves_returnsUpdatedRoom() throws Exception {
        given(userRepository.findByToken("1")).willReturn(user);
        given(roomService.getRoomById("1")).willReturn(rooms.get("1"));
        Room room = rooms.get("1");
        room.setCallerID(1L);
        room.setRoomStatus(RoomStatus.JOINABLE);

        MockHttpServletRequestBuilder getRequest = put("/rooms/1/leave").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(room.getId().intValue())))
                .andExpect(jsonPath("$.roomStatus", is(room.getRoomStatus().toString())))
                .andExpect(jsonPath("$.CallerID", nullValue()))
                .andExpect(jsonPath("$.name", is(room.getName())))
                .andExpect(jsonPath("$.description", is(room.getDescription())));

    }
}

