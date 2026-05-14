package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.MeetingPutDTO;
import ch.uzh.ifi.hase.soprafs26.service.MeetingService;
import ch.uzh.ifi.hase.soprafs26.rest.dto.MeetingPostDTO;
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
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetingController.class)
public class MeetingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeetingService meetingService;

    @MockitoBean
    private UserService userService;

    private User user;
    private Meeting meeting;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("firstname@lastname");
        user.setName("First Last");
        user.setBio("testBio");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now().withNano(0));
        user.setToken("1");

        meeting = new Meeting();
        meeting.setId(1L);
        meeting.setOwner(1L);
        meeting.setInvitedUser(2L);
        meeting.setTitle("test meeting");
        meeting.setDescription("test meeting description");
        meeting.setStartDate(LocalDateTime.now().withNano(0));
        meeting.setEndDate(LocalDateTime.now().plusDays(1).withNano(0));
    }

    @Test
    public void createMeetingTest_validToken() throws Exception {

        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(true);
        given(meetingService.createMeeting(Mockito.any())).willReturn(meeting);

        MeetingPostDTO meetingPostDTO = new MeetingPostDTO();
        meetingPostDTO.setId(1L);
        meetingPostDTO.setTitle("test meeting");
        meetingPostDTO.setDescription("test meeting description");
        meetingPostDTO.setOwner(1L);
        meetingPostDTO.setInvitedUser(2L);
        meetingPostDTO.setStart(meeting.getStartDate());
        meetingPostDTO.setEnd(meeting.getEndDate());



        MockHttpServletRequestBuilder postRequest = post("/meetings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","1")
                .content(asJsonString(meetingPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(meeting.getId().intValue())))
                .andExpect(jsonPath("$.title", is(meeting.getTitle())))
                .andExpect(jsonPath("$.description", is(meeting.getDescription())))
                .andExpect(jsonPath("$.start", is(meeting.getStartDate().toString())))
                .andExpect(jsonPath("$.end", is(meeting.getEndDate().toString())))
                .andExpect(jsonPath("$.owner", is(meeting.getOwner().intValue())))
                .andExpect(jsonPath("$.invitedUser", is(meeting.getInvitedUser().intValue())));

    }

    @Test
    public void createMeetingTest_invalidToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(false);

        mockMvc.perform(post("/meetings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", "2")
                        .content(asJsonString(new MeetingPostDTO())))
                .andExpect(status().isUnauthorized());
        }


    @Test
    public void getMeetingTest_validToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(true);
        List<Meeting> meetings = Collections.singletonList(meeting);
        given(meetingService.getMeetings(Mockito.anyLong())).willReturn(meetings);
        MockHttpServletRequestBuilder getRequest = get("/meetings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","1");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(meeting.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(meeting.getTitle())))
                .andExpect(jsonPath("$[0].description", is(meeting.getDescription())))
                .andExpect(jsonPath("$[0].start", is(meeting.getStartDate().toString())))
                .andExpect(jsonPath("$[0].end", is(meeting.getEndDate().toString())))
                .andExpect(jsonPath("$[0].owner", is(meeting.getOwner().intValue())))
                .andExpect(jsonPath("$[0].invitedUser", is(meeting.getInvitedUser().intValue())));



    }
    @Test
    public void getMeetingTest_invalidToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(false);

        mockMvc.perform(get("/meetings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", "2")
                        .content(asJsonString(new MeetingPostDTO())))
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void getMeetingTest_validTokenEmptyList() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(true);
        List<Meeting> meetings = Collections.emptyList();
        given(meetingService.getMeetings(Mockito.anyLong())).willReturn(meetings);
        MockHttpServletRequestBuilder getRequest = get("/meetings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","1");
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(jsonPath("$", is(Collections.emptyList())));
    }

    @Test
    public void deleteMeetingTest_validToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(true);
        given(meetingService.getMeeting(Mockito.anyLong())).willReturn(meeting);
        MockHttpServletRequestBuilder deleteRequest = delete("/meetings/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","1");
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());

    }

    @Test
    public void deleteMeetingTest_invalidToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(false);
        mockMvc.perform(delete("/meetings/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", "2"))
                        .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateMeetingTest_validToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(true);
        given(meetingService.getMeeting(Mockito.anyLong())).willReturn(meeting);

        MeetingPutDTO meetingPutDTO = new MeetingPutDTO();
        meetingPutDTO.setId(meeting.getId());
        meetingPutDTO.setTitle(meeting.getTitle());
        meetingPutDTO.setDescription(meeting.getDescription());
        meetingPutDTO.setStart(meeting.getStartDate());
        meetingPutDTO.setEnd(meeting.getEndDate());
        meetingPutDTO.setInvitedUser(meeting.getInvitedUser());

        MockHttpServletRequestBuilder putRequest = put("/meetings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","1")
                .content(asJsonString(meetingPutDTO));
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(meeting.getId().intValue())))
                .andExpect(jsonPath("$.title", is(meeting.getTitle())))
                .andExpect(jsonPath("$.description", is(meeting.getDescription())))
                .andExpect(jsonPath("$.start", is(meeting.getStartDate().toString())))
                .andExpect(jsonPath("$.end", is(meeting.getEndDate().toString())))
                .andExpect(jsonPath("$.owner", is(meeting.getOwner().intValue())))
                .andExpect(jsonPath("$.invitedUser", is(meeting.getInvitedUser().intValue())));

    }

    @Test
    public void updateMeetingTest_invalidToken() throws Exception {
        given(userService.token_auth(Mockito.anyString(), Mockito.anyLong()))
                .willReturn(false);
        MockHttpServletRequestBuilder putRequest = put("/meetings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token","2")
                .content(asJsonString(new MeetingPutDTO()));//ensure it reaches the controller
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
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
