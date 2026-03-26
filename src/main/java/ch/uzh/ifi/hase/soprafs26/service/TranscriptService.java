package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.repository.TranscriptRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TranscriptService {

    private final TranscriptRepository transcriptRepository;

    public TranscriptService(@Qualifier("transcriptRepository") TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
    }

    public Transcript createTranscript(Transcript newTranscript) {
        return transcriptRepository.save(newTranscript);
    }

    public List<Transcript> getTranscriptsBySessionId(UUID sessionId) {
        return transcriptRepository.findBySessionId(sessionId);
    }

    public Transcript getTranscriptById(long id) {
        return transcriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transcript not found"));
    }

    // public Transcript updateTranscript(long id, String content) {
    //     Transcript transcript = getTranscriptById(id);
    //     transcript.setContent(content);
    //     return transcriptRepository.save(transcript);
    // }

    public void deleteTranscript(long id) {
        Transcript transcript = getTranscriptById(id);
        transcriptRepository.delete(transcript);
    }
}
