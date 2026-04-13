package ch.uzh.ifi.hase.soprafs26.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import java.util.List;
import java.util.UUID;

@Repository("noteRepository")
public interface NoteRepository extends JpaRepository<Note, Long> {
	List<Note> findBySessionId(UUID sessionId);

	List<Note> findBySessionIdIn(List<UUID> sessionIds);
}
