package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.repository.NoteRepository;
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

public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note testNote;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testNote = new Note();
        testNote.setId(1L);
        testNote.setContent("hello");
        testNote.setSessionId(UUID.randomUUID());
        testNote.setCreatedAt(LocalDateTime.now());
        testNote.setUpdatedAt(LocalDateTime.now());

        Mockito.when(noteRepository.save(Mockito.any())).thenReturn(testNote);
    }

    @Test
    public void createNote_validInput_success() {
        Note created = noteService.createNote(testNote);

        Mockito.verify(noteRepository, Mockito.times(1)).save(Mockito.any());
        assertEquals(testNote.getId(), created.getId());
        assertEquals(testNote.getContent(), created.getContent());
    }

    @Test
    public void getNotesBySessionId_success() {
        Mockito.when(noteRepository.findBySessionId(testNote.getSessionId()))
                .thenReturn(Collections.singletonList(testNote));

        assertEquals(1, noteService.getNotesBySessionId(testNote.getSessionId()).size());
        assertEquals(testNote.getId(), noteService.getNotesBySessionId(testNote.getSessionId()).get(0).getId());
    }

    @Test
    public void getNoteById_noteNotFound_throwsException() {
        Mockito.when(noteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> noteService.getNoteById(999L));
    }

    @Test
    public void getNoteById_success() {
        Mockito.when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        Note foundNote = noteService.getNoteById(1L);
        assertEquals(testNote.getId(), foundNote.getId());
        assertEquals(testNote.getContent(), foundNote.getContent());
    }

    @Test
    public void deleteNote_noteNotFound_throwsException() {
        Mockito.when(noteRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> noteService.deleteNote(999L));
        Mockito.verify(noteRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void getNotesBySessionIds_nullOrEmptyInput_returnsEmptyList() {
        java.util.List<Note> nullNotes = noteService.getNotesBySessionIds(null);
        assertEquals(0, nullNotes.size());
        
        java.util.List<Note> emptyNotes = noteService.getNotesBySessionIds(Collections.emptyList());
        assertEquals(0, emptyNotes.size());
        
        Mockito.verify(noteRepository, Mockito.never()).findBySessionIdIn(Mockito.any());
    }

    @Test
    public void updateNote_success() {
        Mockito.when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        Note updated = noteService.updateNote(1L, "updated");

        assertEquals("updated", updated.getContent());
        Mockito.verify(noteRepository, Mockito.times(1)).save(Mockito.any());
    }
}
