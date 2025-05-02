package com.citationextractor.extractor.citation;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.AnnotatedCitation;
import com.citationextractor.model.Citation;
import com.citationextractor.model.NoteCandidate;

public interface ICitationAnnotator {
    List<AnnotatedCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context);
}
