package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Note;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.NoteGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.NotePostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.NotePutDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @PostMapping("/notes")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public NoteGetDTO createNote(@RequestBody NotePostDTO notePostDTO,
                                 @RequestHeader(value = "token", required = false) String token,
                                 @RequestParam(value = "token", required = false) String tokenParam) {
        String finalToken = token != null ? token : tokenParam;
        User user = validateToken(finalToken);
        Note noteInput = DTOMapper.INSTANCE.convertNotePostDTOtoEntity(notePostDTO);
        Note createdNote = noteService.createNote(noteInput);
        addSessionToUserIfMissing(user, createdNote.getSessionId());
        return DTOMapper.INSTANCE.convertEntityToNoteGetDTO(createdNote);
    }

    @GetMapping("/notes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<NoteGetDTO> getNotesBySession(@RequestParam UUID sessionId, @RequestHeader("token") String token) {
        validateToken(token);
        List<Note> notes = noteService.getNotesBySessionId(sessionId);
        List<NoteGetDTO> noteGetDTOs = new ArrayList<>();
        for (Note note : notes) {
            noteGetDTOs.add(DTOMapper.INSTANCE.convertEntityToNoteGetDTO(note));
        }
        return noteGetDTOs;
    }

    @GetMapping("/notes/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public NoteGetDTO getNoteById(@PathVariable long id, @RequestHeader("token") String token) {
        validateToken(token);
        Note note = noteService.getNoteById(id);
        return DTOMapper.INSTANCE.convertEntityToNoteGetDTO(note);
    }

    @PutMapping("/notes/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public NoteGetDTO updateNote(@PathVariable long id, @RequestBody NotePutDTO notePutDTO, @RequestHeader("token") String token) {
        validateToken(token);
        Note updatedNote = noteService.updateNote(id, notePutDTO.getContent());
        return DTOMapper.INSTANCE.convertEntityToNoteGetDTO(updatedNote);
    }

    @DeleteMapping("/notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteNote(@PathVariable long id, @RequestHeader("token") String token) {
        validateToken(token);
        noteService.deleteNote(id);
    }

    private User validateToken(String token) {
        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
        return userToken;
    }

    private void addSessionToUserIfMissing(User user, UUID sessionId) {
        if (sessionId == null) {
            return;
        }
        if (user.getSessions() == null) {
            user.setSessions(new ArrayList<>());
        }
        if (!user.getSessions().contains(sessionId)) {
            user.getSessions().add(sessionId);
            userRepository.save(user);
        }
    }
}
