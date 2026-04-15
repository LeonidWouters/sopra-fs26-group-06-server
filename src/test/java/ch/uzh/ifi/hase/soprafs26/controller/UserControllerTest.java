package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutPasswordDTO;
import ch.uzh.ifi.hase.soprafs26.room.Room;
import ch.uzh.ifi.hase.soprafs26.room.RoomService;
import ch.uzh.ifi.hase.soprafs26.room.RoomStatus;
import ch.uzh.ifi.hase.soprafs26.service.NoteService;
import ch.uzh.ifi.hase.soprafs26.service.TranscriptService;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

        @MockitoBean
        private NoteService noteService;

        @MockitoBean
        private TranscriptService transcriptService;

    @MockitoBean
    private UserRepository userRepository;

        @MockitoBean
        private RoomService roomService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("firstname@lastname");
        user.setName("First Last");
        user.setBio("testBio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now().withNano(0));
        user.setToken("1");

        List<User> allUsers = Collections.singletonList(user);

        given(userService.getUsers()).willReturn(allUsers);
        given(userRepository.findByToken(Mockito.anyString())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].bio", is(user.getBio())))
                .andExpect(jsonPath("$[0].creationDate", is(user.getCreationDate().withNano(0).toString())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setName("First Last");
        user.setToken("1");
        user.setBio("testBio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now());

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("test");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setName("First Last");
        userPostDTO.setBio("testBio");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.bio", is(user.getBio())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_invalidInput_userNotCreated() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("test");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setName("First Last");
        userPostDTO.setBio("testBio");

        given(userService.createUser(Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Invalid user data"));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isConflict());
    }

    @Test
    public void getAllDocumentsForUser_validInput_returnsDocuments() throws Exception {
        User authenticatedUser = new User();
        authenticatedUser.setToken("1");

        User targetUser = new User();
        targetUser.setId(1L);
        UUID sessionOne = UUID.randomUUID();
        UUID sessionTwo = UUID.randomUUID();
        targetUser.setSessions(Arrays.asList(sessionOne, sessionTwo));

        Transcript transcript = new Transcript();
        transcript.setId(5L);
        transcript.setContent("transcript content");
        transcript.setSessionId(sessionOne);
        transcript.setCreatedAt(LocalDateTime.now().withNano(0));

        Note note = new Note();
        note.setId(9L);
        note.setContent("note content");
        note.setSessionId(sessionTwo);
        note.setCreatedAt(LocalDateTime.now().withNano(0));
        note.setUpdatedAt(LocalDateTime.now().withNano(0));

        given(userRepository.findByToken("1")).willReturn(authenticatedUser);
        given(userService.getByID(1L)).willReturn(targetUser);
        given(transcriptService.getTranscriptsBySessionIds(targetUser.getSessions()))
                .willReturn(Collections.singletonList(transcript));
        given(noteService.getNotesBySessionIds(targetUser.getSessions()))
                .willReturn(Collections.singletonList(note));

        MockHttpServletRequestBuilder getRequest = get("/users/1/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transcripts", hasSize(1)))
                .andExpect(jsonPath("$.transcripts[0].content", is(transcript.getContent())))
                .andExpect(jsonPath("$.notes", hasSize(1)))
                .andExpect(jsonPath("$.notes[0].content", is(note.getContent())));
    }

    @Test
    public void getAllTranscriptsForUser_validInput_returnsTranscripts() throws Exception {
        User authenticatedUser = new User();
        authenticatedUser.setToken("1");

        User targetUser = new User();
        targetUser.setId(1L);
        UUID sessionId = UUID.randomUUID();
        targetUser.setSessions(Collections.singletonList(sessionId));

        Transcript transcript = new Transcript();
        transcript.setId(5L);
        transcript.setContent("transcript content");
        transcript.setSessionId(sessionId);
        transcript.setCreatedAt(LocalDateTime.now().withNano(0));

        given(userRepository.findByToken("1")).willReturn(authenticatedUser);
        given(userService.getByID(1L)).willReturn(targetUser);
        given(transcriptService.getTranscriptsBySessionIds(targetUser.getSessions()))
                .willReturn(Collections.singletonList(transcript));

        MockHttpServletRequestBuilder getRequest = get("/users/1/transcripts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is(transcript.getContent())));
    }

    @Test
    public void getAllNotesForUser_validInput_returnsNotes() throws Exception {
        User authenticatedUser = new User();
        authenticatedUser.setToken("1");

        User targetUser = new User();
        targetUser.setId(1L);
        UUID sessionId = UUID.randomUUID();
        targetUser.setSessions(Collections.singletonList(sessionId));

        Note note = new Note();
        note.setId(9L);
        note.setContent("note content");
        note.setSessionId(sessionId);
        note.setCreatedAt(LocalDateTime.now().withNano(0));
        note.setUpdatedAt(LocalDateTime.now().withNano(0));

        given(userRepository.findByToken("1")).willReturn(authenticatedUser);
        given(userService.getByID(1L)).willReturn(targetUser);
        given(noteService.getNotesBySessionIds(targetUser.getSessions()))
                .willReturn(Collections.singletonList(note));

        MockHttpServletRequestBuilder getRequest = get("/users/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is(note.getContent())));
    }

    @Test
    public void changePassword_validInput_passwordChanged() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setName("First Last");
        user.setToken("1");
        user.setPassword("testPassword");

        given(userRepository.findByToken("1")).willReturn(user);

        UserPutPasswordDTO userPutDTO = new UserPutPasswordDTO();
        userPutDTO.setPassword("newTestPassword");

        MockHttpServletRequestBuilder putRequest = put("/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1")
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void changePassword_UserDoesNotExist() throws Exception {
        given(userRepository.findByToken("1")).willReturn(null);

        UserPutPasswordDTO userPutDTO = new UserPutPasswordDTO();
        userPutDTO.setPassword("newTestPassword");

        MockHttpServletRequestBuilder putRequest = put("/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1")
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void logout_withTokenParam_removesUserFromFullRoomAndSetsOffline() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setRoomId(1L);

        Room room = Room.createRoom(1L, "room1", "desc");
        room.setRoomStatus(RoomStatus.FULL);
        room.setCallerID(1L);
        room.setCalleeID(2L);
        room.setBaseTranscript("abc");
        room.setBaseNote("xyz");

        given(userRepository.findByToken("1")).willReturn(user);
        given(roomService.getRoomById("1")).willReturn(room);

        MockHttpServletRequestBuilder logoutRequest = post("/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "1");

        mockMvc.perform(logoutRequest)
                .andExpect(status().isNoContent());

        verify(userRepository).save(user);
        verify(noteService).createNote(Mockito.any(Note.class));
        verify(transcriptService).createTranscript(Mockito.any(Transcript.class));
        org.junit.jupiter.api.Assertions.assertNull(user.getRoomId());
        org.junit.jupiter.api.Assertions.assertEquals(UserStatus.OFFLINE, user.getStatus());
        org.junit.jupiter.api.Assertions.assertNotEquals("1", user.getToken());
        org.junit.jupiter.api.Assertions.assertNull(room.getCallerID());
        org.junit.jupiter.api.Assertions.assertEquals(2L, room.getCalleeID());
        org.junit.jupiter.api.Assertions.assertEquals(RoomStatus.JOINABLE, room.getRoomStatus());
    }

    @Test
    public void logout_whenLastParticipantLeaves_setsRoomEmptyAndClearsBuffers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setRoomId(1L);

        Room room = Room.createRoom(1L, "room1", "desc");
        room.setRoomStatus(RoomStatus.JOINABLE);
        room.setCallerID(1L);
        room.setBaseTranscript("abc");
        room.setBaseNote("xyz");

        given(userRepository.findByToken("1")).willReturn(user);
        given(roomService.getRoomById("1")).willReturn(room);

        MockHttpServletRequestBuilder logoutRequest = post("/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1");

        mockMvc.perform(logoutRequest)
                .andExpect(status().isNoContent());

        verify(userRepository).save(user);
        verify(noteService).createNote(Mockito.any(Note.class));
        verify(transcriptService).createTranscript(Mockito.any(Transcript.class));
        org.junit.jupiter.api.Assertions.assertNull(room.getCallerID());
        org.junit.jupiter.api.Assertions.assertNull(room.getCalleeID());
        org.junit.jupiter.api.Assertions.assertEquals(RoomStatus.EMPTY, room.getRoomStatus());
        org.junit.jupiter.api.Assertions.assertEquals("", room.getBaseTranscript());
        org.junit.jupiter.api.Assertions.assertEquals("", room.getBaseNote());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }
}
