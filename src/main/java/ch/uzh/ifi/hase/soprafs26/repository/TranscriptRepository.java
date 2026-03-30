package ch.uzh.ifi.hase.soprafs26.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs26.entity.Transcript;

import java.util.List;
import java.util.UUID;

@Repository("transcriptRepository")
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    List<Transcript> findBySessionId(UUID sessionId);
}
