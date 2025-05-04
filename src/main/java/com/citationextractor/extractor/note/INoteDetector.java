package com.citationextractor.extractor.note;

import java.util.List;

import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;

public interface INoteDetector {
    List<NoteCandidate> getNoteCandidates(ExtractionContext context);
    List<NoteCandidate> getTradNoteCandidates(ExtractionContext context);
    List<NoteCandidate> getHarvardNoteCandidates(ExtractionContext context);
}
