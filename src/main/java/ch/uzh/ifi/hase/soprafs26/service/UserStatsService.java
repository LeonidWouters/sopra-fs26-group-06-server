package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.entity.UserStats;
import ch.uzh.ifi.hase.soprafs26.repository.NoteRepository;
import ch.uzh.ifi.hase.soprafs26.repository.TranscriptRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserStatsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserStatsService {

    private final UserStatsRepository userStatsRepository;
    private final UserRepository userRepository;
    private final TranscriptRepository transcriptRepository;
    private final NoteRepository noteRepository;

    public UserStatsService(UserStatsRepository userStatsRepository,
                            UserRepository userRepository,
                            TranscriptRepository transcriptRepository,
                            NoteRepository noteRepository) {
        this.userStatsRepository = userStatsRepository;
        this.userRepository = userRepository;
        this.transcriptRepository = transcriptRepository;
        this.noteRepository = noteRepository;
    }

    public UserStats getStats(Long userId) {
        User user = userRepository.findByid(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<UUID> sessions = user.getSessions() != null ? user.getSessions() : new ArrayList<>();
        int totalSessions = sessions.size();
        int totalTranscripts = sessions.isEmpty() ? 0 : transcriptRepository.findBySessionIdIn(sessions).size();
        int totalNotes = sessions.isEmpty() ? 0 : noteRepository.findBySessionIdIn(sessions).size();
        int totalFriends = user.getFriends() != null ? user.getFriends().size() : 0;

        UserStats stats = userStatsRepository.findByUserId(userId);
        if (stats == null) {
            stats = new UserStats();
            stats.setUserId(userId);
        }

        stats.setTotalSessions(totalSessions);
        stats.setTotalTranscripts(totalTranscripts);
        stats.setTotalNotes(totalNotes);
        stats.setTotalFriends(totalFriends);
        stats.setComputedAt(LocalDateTime.now());

        return userStatsRepository.save(stats);
    }
}
