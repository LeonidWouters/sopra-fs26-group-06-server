package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.NotePostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.NotePutDTO;
import ch.uzh.ifi.hase.soprafs26.service.NoteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void createNote_validInput_noteCreated() throws Exception {
        UUID sessionId = UUID.randomUUID();

        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        Note note = new Note();
        note.setId(1L);
        note.setContent("note content");
        note.setSessionId(sessionId);
        note.setCreatedAt(LocalDateTime.now().withNano(0));
        note.setUpdatedAt(LocalDateTime.now().withNano(0));

        NotePostDTO notePostDTO = new NotePostDTO();
        notePostDTO.setContent("note content");
        notePostDTO.setSessionId(sessionId);

        given(noteService.createNote(Mockito.any())).willReturn(note);

        MockHttpServletRequestBuilder postRequest = post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1")
                .content(asJsonString(notePostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(note.getId().intValue())))
                .andExpect(jsonPath("$.content", is(note.getContent())));

            Mockito.verify(userRepository).save(user);
    }

            @Test
            public void createNote_withTokenQueryParam_noteCreated() throws Exception {
            UUID sessionId = UUID.randomUUID();

            User user = new User();
            user.setToken("1");
            given(userRepository.findByToken("1")).willReturn(user);

            Note note = new Note();
            note.setId(2L);
            note.setContent("note content query");
            note.setSessionId(sessionId);
            note.setCreatedAt(LocalDateTime.now().withNano(0));
            note.setUpdatedAt(LocalDateTime.now().withNano(0));

            NotePostDTO notePostDTO = new NotePostDTO();
            notePostDTO.setContent("note content query");
            notePostDTO.setSessionId(sessionId);

            given(noteService.createNote(Mockito.any())).willReturn(note);

            MockHttpServletRequestBuilder postRequest = post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "1")
                .content(asJsonString(notePostDTO));

            mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(note.getId().intValue())))
                .andExpect(jsonPath("$.content", is(note.getContent())));

            Mockito.verify(userRepository).save(user);
            }

    @Test
    public void getNoteById_validInput_returnsNote() throws Exception {
        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        Note note = new Note();
        note.setId(1L);
        note.setContent("test single note");
        note.setSessionId(UUID.randomUUID());

        given(noteService.getNoteById(1L)).willReturn(note);

        MockHttpServletRequestBuilder getRequest = get("/notes/1")
                .header("token", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(note.getId().intValue())))
                .andExpect(jsonPath("$.content", is(note.getContent())));
    }

    @Test
    public void getNotesBySession_validToken_returnsNotes() throws Exception {
        UUID sessionId = UUID.randomUUID();

        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        Note note = new Note();
        note.setId(1L);
        note.setContent("hello");
        note.setSessionId(sessionId);
        note.setCreatedAt(LocalDateTime.now().withNano(0));
        note.setUpdatedAt(LocalDateTime.now().withNano(0));

        given(noteService.getNotesBySessionId(sessionId)).willReturn(Collections.singletonList(note));

        MockHttpServletRequestBuilder getRequest = get("/notes")
                .param("sessionId", sessionId.toString())
                .header("token", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is(note.getContent())));
    }

    @Test
    public void updateNote_userUnauthorized_returns401() throws Exception {
        given(userRepository.findByToken("missing")).willReturn(null);

        NotePutDTO notePutDTO = new NotePutDTO();
        notePutDTO.setContent("updated content");

        MockHttpServletRequestBuilder putRequest = put("/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "missing")
                .content(asJsonString(notePutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteNote_validInput_noContent() throws Exception {
        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        MockHttpServletRequestBuilder deleteRequest = delete("/notes/1")
                .header("token", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
