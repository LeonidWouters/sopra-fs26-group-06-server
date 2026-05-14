package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class MeetingRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeetingRepository meetingRepository;

    private Meeting meeting;

    @BeforeEach
    public void setup() {
        meeting = new Meeting();
        meeting.setOwner(1L);
        meeting.setInvitedUser(2L);
        meeting.setTitle("test meeting");
        meeting.setDescription("test meeting description");
        meeting.setStartDate(LocalDateTime.now());
        meeting.setEndDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void findByOwner_valid() {

        entityManager.persist(meeting);
        entityManager.flush();

        Meeting found = meetingRepository.findByOwner(meeting.getOwner()).get(0);

        assertEquals(found.getId(),meeting.getId());
        assertEquals(found.getOwner(), meeting.getOwner());
        assertEquals(found.getInvitedUser(), meeting.getInvitedUser());
        assertEquals(found.getTitle(), meeting.getTitle());
        assertEquals(found.getDescription(), meeting.getDescription());
        assertEquals(found.getStartDate(), meeting.getStartDate());
        assertEquals(found.getEndDate(), meeting.getEndDate());



    }

    @Test
    public void findByInvitedUser_valid() {
        entityManager.persist(meeting);
        entityManager.flush();

        Meeting found = meetingRepository.findByInvitedUser(meeting.getInvitedUser()).get(0);

        assertEquals(found.getId(),meeting.getId());
        assertEquals(found.getOwner(), meeting.getOwner());
        assertEquals(found.getInvitedUser(), meeting.getInvitedUser());
        assertEquals(found.getTitle(), meeting.getTitle());
        assertEquals(found.getDescription(), meeting.getDescription());
        assertEquals(found.getStartDate(), meeting.getStartDate());
        assertEquals(found.getEndDate(), meeting.getEndDate());
    }

    @Test
    public void findByid_valid() {
        entityManager.persist(meeting);
        entityManager.flush();

        Meeting found = meetingRepository.findByid(meeting.getId());

        assertEquals(found.getId(),meeting.getId());
        assertEquals(found.getOwner(), meeting.getOwner());
        assertEquals(found.getInvitedUser(), meeting.getInvitedUser());
        assertEquals(found.getTitle(), meeting.getTitle());
        assertEquals(found.getDescription(), meeting.getDescription());
        assertEquals(found.getStartDate(), meeting.getStartDate());
        assertEquals(found.getEndDate(), meeting.getEndDate());
    }
}
