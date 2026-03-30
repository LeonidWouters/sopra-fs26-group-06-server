package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Transcript;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TranscriptGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TranscriptPostDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranscriptDTOMapperTest {

    @Test
    public void testCreateTranscript_fromTranscriptPostDTO_toTranscript_success() {
        TranscriptPostDTO transcriptPostDTO = new TranscriptPostDTO();
        transcriptPostDTO.setContent("test transcript");
        transcriptPostDTO.setSessionId(UUID.randomUUID());

        Transcript transcript = DTOMapper.INSTANCE.convertTranscriptPostDTOtoEntity(transcriptPostDTO);

        assertEquals(transcriptPostDTO.getContent(), transcript.getContent());
        assertEquals(transcriptPostDTO.getSessionId(), transcript.getSessionId());
    }

    @Test
    public void testGetTranscript_fromTranscript_toTranscriptGetDTO_success() {
        Transcript transcript = new Transcript();
        transcript.setId(1L);
        transcript.setContent("mapped transcript");
        transcript.setSessionId(UUID.randomUUID());
        transcript.setCreatedAt(LocalDateTime.now());

        TranscriptGetDTO transcriptGetDTO = DTOMapper.INSTANCE.convertEntityToTranscriptGetDTO(transcript);

        assertEquals(transcript.getId(), transcriptGetDTO.getId());
        assertEquals(transcript.getContent(), transcriptGetDTO.getContent());
        assertEquals(transcript.getSessionId(), transcriptGetDTO.getSessionId());
        assertEquals(transcript.getCreatedAt(), transcriptGetDTO.getCreatedAt());
    }
}
