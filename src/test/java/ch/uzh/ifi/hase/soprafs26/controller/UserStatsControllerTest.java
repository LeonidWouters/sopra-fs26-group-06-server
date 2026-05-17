package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.entity.UserStats;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.service.UserStatsService;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserStatsController.class)
public class UserStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserStatsService userStatsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void getUserStats_validToken_returnsStats() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setToken("valid-token");

        UserStats stats = new UserStats();
        stats.setUserId(1L);
        stats.setTotalSessions(3);
        stats.setTotalTranscripts(2);
        stats.setTotalNotes(5);
        stats.setTotalFriends(4);
        stats.setComputedAt(LocalDateTime.now().withNano(0));

        given(userRepository.findByToken("valid-token")).willReturn(authenticatedUser);
        given(userStatsService.getStats(1L)).willReturn(stats);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "valid-token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(stats.getUserId().intValue())))
                .andExpect(jsonPath("$.totalSessions", is(stats.getTotalSessions())))
                .andExpect(jsonPath("$.totalTranscripts", is(stats.getTotalTranscripts())))
                .andExpect(jsonPath("$.totalNotes", is(stats.getTotalNotes())))
                .andExpect(jsonPath("$.totalFriends", is(stats.getTotalFriends())));
    }

    @Test
    public void getUserStats_invalidToken_returnsUnauthorized() throws Exception {
        // given
        given(userRepository.findByToken(Mockito.anyString())).willReturn(null);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "invalid-token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserStats_userNotFound_returnsNotFound() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setToken("valid-token");

        given(userRepository.findByToken("valid-token")).willReturn(authenticatedUser);
        given(userStatsService.getStats(1L))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "valid-token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserStats_missingTokenHeader_returnsBadRequest() throws Exception {
        // when — no token header sent
        MockHttpServletRequestBuilder getRequest = get("/users/1/stats")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest());
    }
}
