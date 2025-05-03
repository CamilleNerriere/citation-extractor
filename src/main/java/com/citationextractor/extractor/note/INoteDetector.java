package com.citationextractor.extractor.note;

import java.util.List;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.citation.NoteCandidate;

public interface INoteDetector {
    List<NoteCandidate> getNoteCandidates(ExtractionContext context);
    List<NoteCandidate> getTradNoteCandidates(ExtractionContext context);
    List<NoteCandidate> getHarvardNoteCandidates(ExtractionContext context);
}
