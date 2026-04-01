package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.util.List;

public class UserDocumentsGetDTO {

    private List<TranscriptGetDTO> transcripts;
    private List<NoteGetDTO> notes;

    public List<TranscriptGetDTO> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(List<TranscriptGetDTO> transcripts) {
        this.transcripts = transcripts;
    }

    public List<NoteGetDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteGetDTO> notes) {
        this.notes = notes;
    }
}
