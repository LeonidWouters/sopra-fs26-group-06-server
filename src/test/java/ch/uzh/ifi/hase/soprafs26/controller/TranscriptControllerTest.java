package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TranscriptPostDTO;
import ch.uzh.ifi.hase.soprafs26.service.TranscriptService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TranscriptController.class)
public class TranscriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TranscriptService transcriptService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void createTranscript_validInput_transcriptCreated() throws Exception {
        UUID sessionId = UUID.randomUUID();

        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        Transcript transcript = new Transcript();
        transcript.setId(1L);
        transcript.setContent("transcript content");
        transcript.setSessionId(sessionId);
        transcript.setCreatedAt(LocalDateTime.now().withNano(0));

        TranscriptPostDTO transcriptPostDTO = new TranscriptPostDTO();
        transcriptPostDTO.setContent("transcript content");
        transcriptPostDTO.setSessionId(sessionId);

        given(transcriptService.createTranscript(Mockito.any())).willReturn(transcript);

        MockHttpServletRequestBuilder postRequest = post("/transcripts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1")
                .content(asJsonString(transcriptPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(transcript.getId().intValue())))
                .andExpect(jsonPath("$.content", is(transcript.getContent())));

            Mockito.verify(userRepository).save(user);
    }

            @Test
            public void createTranscript_withTokenQueryParam_transcriptCreated() throws Exception {
            UUID sessionId = UUID.randomUUID();

            User user = new User();
            user.setToken("1");
            given(userRepository.findByToken("1")).willReturn(user);

            Transcript transcript = new Transcript();
            transcript.setId(2L);
            transcript.setContent("transcript content query");
            transcript.setSessionId(sessionId);
            transcript.setCreatedAt(LocalDateTime.now().withNano(0));

            TranscriptPostDTO transcriptPostDTO = new TranscriptPostDTO();
            transcriptPostDTO.setContent("transcript content query");
            transcriptPostDTO.setSessionId(sessionId);

            given(transcriptService.createTranscript(Mockito.any())).willReturn(transcript);

            MockHttpServletRequestBuilder postRequest = post("/transcripts")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "1")
                .content(asJsonString(transcriptPostDTO));

            mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(transcript.getId().intValue())))
                .andExpect(jsonPath("$.content", is(transcript.getContent())));

            Mockito.verify(userRepository).save(user);
            }

    @Test
    public void getTranscriptsBySession_validToken_returnsTranscripts() throws Exception {
        UUID sessionId = UUID.randomUUID();

        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        Transcript transcript = new Transcript();
        transcript.setId(1L);
        transcript.setContent("hello transcript");
        transcript.setSessionId(sessionId);
        transcript.setCreatedAt(LocalDateTime.now().withNano(0));

        given(transcriptService.getTranscriptsBySessionId(sessionId)).willReturn(Collections.singletonList(transcript));

        MockHttpServletRequestBuilder getRequest = get("/transcripts")
                .param("sessionId", sessionId.toString())
                .header("token", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is(transcript.getContent())));
    }

    @Test
    public void getTranscriptById_validInput_returnsTranscript() throws Exception {
        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        Transcript transcript = new Transcript();
        transcript.setId(1L);
        transcript.setContent("test single transcript");
        transcript.setSessionId(UUID.randomUUID());

        given(transcriptService.getTranscriptById(1L)).willReturn(transcript);

        MockHttpServletRequestBuilder getRequest = get("/transcripts/1")
                .header("token", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(transcript.getId().intValue())))
                .andExpect(jsonPath("$.content", is(transcript.getContent())));
    }

    @Test
    public void deleteTranscript_validInput_noContent() throws Exception {
        User user = new User();
        user.setToken("1");
        given(userRepository.findByToken("1")).willReturn(user);

        MockHttpServletRequestBuilder deleteRequest = delete("/transcripts/1")
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
