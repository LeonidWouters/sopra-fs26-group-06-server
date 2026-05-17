package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.entity.UserStats;
import ch.uzh.ifi.hase.soprafs26.repository.NoteRepository;
import ch.uzh.ifi.hase.soprafs26.repository.TranscriptRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserStatsServiceTest {

    @Mock
    private UserStatsRepository userStatsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TranscriptRepository transcriptRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private UserStatsService userStatsService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");

        UUID session1 = UUID.randomUUID();
        UUID session2 = UUID.randomUUID();
        testUser.setSessions(Arrays.asList(session1, session2));
        testUser.setFriends(Arrays.asList(2L, 3L));
    }

    @Test
    public void getStats_userExists_returnsCorrectCounts() {
        // given
        List<UUID> sessions = testUser.getSessions();

        Transcript transcript = new Transcript();
        transcript.setId(10L);

        Note note = new Note();
        note.setId(20L);

        UserStats savedStats = new UserStats();
        savedStats.setUserId(testUser.getId());
        savedStats.setTotalSessions(2);
        savedStats.setTotalTranscripts(1);
        savedStats.setTotalNotes(1);
        savedStats.setTotalFriends(2);

        Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
        Mockito.when(userStatsRepository.findByUserId(testUser.getId())).thenReturn(null);
        Mockito.when(transcriptRepository.findBySessionIdIn(sessions))
                .thenReturn(Collections.singletonList(transcript));
        Mockito.when(noteRepository.findBySessionIdIn(sessions))
                .thenReturn(Collections.singletonList(note));
        Mockito.when(userStatsRepository.save(Mockito.any())).thenReturn(savedStats);

        // when
        UserStats result = userStatsService.getStats(testUser.getId());

        // then
        Mockito.verify(userStatsRepository, Mockito.times(1)).save(Mockito.any());
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals(2, result.getTotalSessions());
        assertEquals(1, result.getTotalTranscripts());
        assertEquals(1, result.getTotalNotes());
        assertEquals(2, result.getTotalFriends());
    }

    @Test
    public void getStats_userNotFound_throwsException() {
        // given
        Mockito.when(userRepository.findByid(999L)).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> userStatsService.getStats(999L));
    }

    @Test
    public void getStats_nullSessions_doesNotQueryReposAndReturnsZero() {
        // given
        testUser.setSessions(null);

        UserStats savedStats = new UserStats();
        savedStats.setUserId(testUser.getId());
        savedStats.setTotalSessions(0);
        savedStats.setTotalTranscripts(0);
        savedStats.setTotalNotes(0);
        savedStats.setTotalFriends(2);

        Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
        Mockito.when(userStatsRepository.findByUserId(testUser.getId())).thenReturn(null);
        Mockito.when(userStatsRepository.save(Mockito.any())).thenReturn(savedStats);

        // when
        UserStats result = userStatsService.getStats(testUser.getId());

        // then
        assertEquals(0, result.getTotalSessions());
        assertEquals(0, result.getTotalTranscripts());
        assertEquals(0, result.getTotalNotes());
        Mockito.verify(transcriptRepository, Mockito.never()).findBySessionIdIn(Mockito.any());
        Mockito.verify(noteRepository, Mockito.never()).findBySessionIdIn(Mockito.any());
    }

    @Test
    public void getStats_emptySessions_doesNotQueryReposAndReturnsZero() {
        // given
        testUser.setSessions(Collections.emptyList());

        UserStats savedStats = new UserStats();
        savedStats.setUserId(testUser.getId());
        savedStats.setTotalSessions(0);
        savedStats.setTotalTranscripts(0);
        savedStats.setTotalNotes(0);
        savedStats.setTotalFriends(2);

        Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
        Mockito.when(userStatsRepository.findByUserId(testUser.getId())).thenReturn(null);
        Mockito.when(userStatsRepository.save(Mockito.any())).thenReturn(savedStats);

        // when
        UserStats result = userStatsService.getStats(testUser.getId());

        // then
        assertEquals(0, result.getTotalSessions());
        assertEquals(0, result.getTotalTranscripts());
        assertEquals(0, result.getTotalNotes());
        Mockito.verify(transcriptRepository, Mockito.never()).findBySessionIdIn(Mockito.any());
        Mockito.verify(noteRepository, Mockito.never()).findBySessionIdIn(Mockito.any());
    }

    @Test
    public void getStats_nullFriends_returnsZeroFriends() {
        // given
        testUser.setFriends(null);
        List<UUID> sessions = testUser.getSessions();

        UserStats savedStats = new UserStats();
        savedStats.setUserId(testUser.getId());
        savedStats.setTotalSessions(2);
        savedStats.setTotalFriends(0);

        Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
        Mockito.when(userStatsRepository.findByUserId(testUser.getId())).thenReturn(null);
        Mockito.when(transcriptRepository.findBySessionIdIn(sessions)).thenReturn(Collections.emptyList());
        Mockito.when(noteRepository.findBySessionIdIn(sessions)).thenReturn(Collections.emptyList());
        Mockito.when(userStatsRepository.save(Mockito.any())).thenReturn(savedStats);

        // when
        UserStats result = userStatsService.getStats(testUser.getId());

        // then
        assertEquals(0, result.getTotalFriends());
    }

    @Test
    public void getStats_existingStatsRecord_updatesInsteadOfCreatingNew() {
        // given
        List<UUID> sessions = testUser.getSessions();

        UserStats existingStats = new UserStats();
        existingStats.setUserId(testUser.getId());
        existingStats.setTotalSessions(0);
        existingStats.setTotalTranscripts(0);
        existingStats.setTotalNotes(0);
        existingStats.setTotalFriends(0);

        Mockito.when(userRepository.findByid(testUser.getId())).thenReturn(testUser);
        Mockito.when(userStatsRepository.findByUserId(testUser.getId())).thenReturn(existingStats);
        Mockito.when(transcriptRepository.findBySessionIdIn(sessions)).thenReturn(Collections.emptyList());
        Mockito.when(noteRepository.findBySessionIdIn(sessions)).thenReturn(Collections.emptyList());
        Mockito.when(userStatsRepository.save(Mockito.any())).thenReturn(existingStats);

        // when
        userStatsService.getStats(testUser.getId());

        // then — save is called exactly once with the existing record, not a newly created one
        Mockito.verify(userStatsRepository, Mockito.times(1)).save(existingStats);
    }
}
