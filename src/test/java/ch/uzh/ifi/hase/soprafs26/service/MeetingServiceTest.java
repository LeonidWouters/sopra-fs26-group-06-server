package ch.uzh.ifi.hase.soprafs26.service;


import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import ch.uzh.ifi.hase.soprafs26.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

public class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private MeetingService meetingService;

    private Meeting meeting;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        meeting = new Meeting();
        meeting.setId(1L);
        meeting.setOwner(1L);
        meeting.setInvitedUser(2L);
        meeting.setTitle("test meeting");
        meeting.setDescription("test meeting description");
        meeting.setStartDate(LocalDateTime.now());
        meeting.setEndDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testCreateMeeting() {
        Meeting created = meetingService.createMeeting(meeting);
        Mockito.verify(meetingRepository).save(meeting);

        assertEquals(created.getId(),meeting.getId());
        assertEquals(created.getOwner(), meeting.getOwner());
        assertEquals(created.getInvitedUser(), meeting.getInvitedUser());
        assertEquals(created.getTitle(), meeting.getTitle());
        assertEquals(created.getDescription(), meeting.getDescription());
        assertEquals(created.getStartDate(), meeting.getStartDate());
        assertEquals(created.getEndDate(), meeting.getEndDate());
    }

    @Test
    public void testGetMeetings(){
        Meeting owned = new Meeting();
        Meeting invited = new Meeting();

        given(meetingRepository.findByOwner(1L)).willReturn(new ArrayList<>(List.of(owned)));
        given(meetingRepository.findByInvitedUser(1L)).willReturn(new ArrayList<>(List.of(invited)));

        List<Meeting> result = meetingService.getMeetings(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(owned));
        assertTrue(result.contains(invited));
    }

    @Test
    void getMeetings_returnsEmptyList() {

        meetingService.deleteMeeting(meeting);
        given(meetingRepository.findByOwner(1L)).willReturn(Collections.emptyList());
        given(meetingRepository.findByInvitedUser(1L)).willReturn(Collections.emptyList());

        List<Meeting> result = meetingService.getMeetings(1L);

        assertEquals(0, result.size());
    }
}
