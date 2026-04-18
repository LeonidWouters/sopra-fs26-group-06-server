package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.repository.TranscriptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TranscriptServiceTest {

    @Mock
    private TranscriptRepository transcriptRepository;

    @InjectMocks
    private TranscriptService transcriptService;

    private Transcript testTranscript;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testTranscript = new Transcript();
        testTranscript.setId(1L);
        testTranscript.setContent("hello");
        testTranscript.setSessionId(UUID.randomUUID());
        testTranscript.setCreatedAt(LocalDateTime.now());

        Mockito.when(transcriptRepository.save(Mockito.any())).thenReturn(testTranscript);
    }

    @Test
    public void createTranscript_validInput_success() {
        Transcript created = transcriptService.createTranscript(testTranscript);

        Mockito.verify(transcriptRepository, Mockito.times(1)).save(Mockito.any());
        assertEquals(testTranscript.getId(), created.getId());
        assertEquals(testTranscript.getContent(), created.getContent());
    }

    @Test
    public void getTranscriptsBySessionId_success() {
        Mockito.when(transcriptRepository.findBySessionId(testTranscript.getSessionId()))
                .thenReturn(Collections.singletonList(testTranscript));

        assertEquals(1, transcriptService.getTranscriptsBySessionId(testTranscript.getSessionId()).size());
        assertEquals(testTranscript.getId(), transcriptService.getTranscriptsBySessionId(testTranscript.getSessionId()).get(0).getId());
    }

    @Test
    public void getTranscriptById_transcriptNotFound_throwsException() {
        Mockito.when(transcriptRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> transcriptService.getTranscriptById(999L));
    }

    @Test
    public void getTranscriptById_success() {
        Mockito.when(transcriptRepository.findById(1L)).thenReturn(Optional.of(testTranscript));
        Transcript found = transcriptService.getTranscriptById(1L);
        assertEquals(testTranscript.getId(), found.getId());
        assertEquals(testTranscript.getContent(), found.getContent());
    }

    @Test
    public void getTranscriptsBySessionIds_nullOrEmptyInput_returnsEmptyList() {
        java.util.List<Transcript> nullTranscripts = transcriptService.getTranscriptsBySessionIds(null);
        assertEquals(0, nullTranscripts.size());

        java.util.List<Transcript> emptyTranscripts = transcriptService.getTranscriptsBySessionIds(Collections.emptyList());
        assertEquals(0, emptyTranscripts.size());

        Mockito.verify(transcriptRepository, Mockito.never()).findBySessionIdIn(Mockito.any());
    }

    @Test
    public void deleteTranscript_transcriptNotFound_throwsException() {
        Mockito.when(transcriptRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> transcriptService.deleteTranscript(999L));
        Mockito.verify(transcriptRepository, Mockito.never()).delete(Mockito.any());
    }
}
