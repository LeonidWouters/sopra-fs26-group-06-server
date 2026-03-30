package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TranscriptGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TranscriptPostDTO;
// import ch.uzh.ifi.hase.soprafs26.rest.dto.TranscriptPutDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.TranscriptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class TranscriptController {

    private final TranscriptService transcriptService;
    private final UserRepository userRepository;

    TranscriptController(TranscriptService transcriptService, UserRepository userRepository) {
        this.transcriptService = transcriptService;
        this.userRepository = userRepository;
    }

    @PostMapping("/transcripts")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TranscriptGetDTO createTranscript(@RequestBody TranscriptPostDTO transcriptPostDTO,
                                             @RequestHeader("token") String token) {
        validateToken(token);
        Transcript transcriptInput = DTOMapper.INSTANCE.convertTranscriptPostDTOtoEntity(transcriptPostDTO);
        Transcript createdTranscript = transcriptService.createTranscript(transcriptInput);
        return DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(createdTranscript);
    }

    @GetMapping("/transcripts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TranscriptGetDTO> getTranscriptsBySession(@RequestParam UUID sessionId,
                                                          @RequestHeader("token") String token) {
        validateToken(token);
        List<Transcript> transcripts = transcriptService.getTranscriptsBySessionId(sessionId);
        List<TranscriptGetDTO> transcriptGetDTOs = new ArrayList<>();
        for (Transcript transcript : transcripts) {
            transcriptGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(transcript));
        }
        return transcriptGetDTOs;
    }

    @GetMapping("/transcripts/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TranscriptGetDTO getTranscriptById(@PathVariable long id, @RequestHeader("token") String token) {
        validateToken(token);
        Transcript transcript = transcriptService.getTranscriptById(id);
        return DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(transcript);
    }

    // @PutMapping("/transcripts/{id}")
    // @ResponseStatus(HttpStatus.OK)
    // @ResponseBody
    // public TranscriptGetDTO updateTranscript(@PathVariable long id,
    //                                          @RequestBody TranscriptPutDTO transcriptPutDTO,
    //                                          @RequestHeader("token") String token) {
    //     validateToken(token);
    //     Transcript updatedTranscript = transcriptService.updateTranscript(id, transcriptPutDTO.getContent());
    //     return DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(updatedTranscript);
    // }

    @DeleteMapping("/transcripts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteTranscript(@PathVariable long id, @RequestHeader("token") String token) {
        validateToken(token);
        transcriptService.deleteTranscript(id);
    }

    private void validateToken(String token) {
        User userToken = userRepository.findByToken(token);
        if (userToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found");
        }
    }
}
