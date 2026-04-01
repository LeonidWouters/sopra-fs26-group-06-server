package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(@Qualifier("noteRepository") NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(Note newNote) {
        return noteRepository.save(newNote);
    }

    public List<Note> getNotesBySessionId(UUID sessionId) {
        return noteRepository.findBySessionId(sessionId);
    }

    public List<Note> getNotesBySessionIds(List<UUID> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }
        return noteRepository.findBySessionIdIn(sessionIds);
    }

    public Note getNoteById(long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    public Note updateNote(long id, String content) {
        Note note = getNoteById(id);
        note.setContent(content);
        return noteRepository.save(note);
    }

    public void deleteNote(long id) {
        Note note = getNoteById(id);
        noteRepository.delete(note);
    }
}
