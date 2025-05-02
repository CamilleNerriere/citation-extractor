package com.citationextractor.extractor.note;

import java.util.List;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.NoteCandidate;

public interface INoteDetector {
    List<NoteCandidate> getNoteCandidates(ExtractionContext context);
}
