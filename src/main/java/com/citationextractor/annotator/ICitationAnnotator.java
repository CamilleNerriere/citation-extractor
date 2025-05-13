package com.citationextractor.annotator;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;

public interface ICitationAnnotator<C, A> {
    List<A> getAnnotatedCitations(
        LinkedHashMap<Integer, List<C>> citationsCandidatesPerPage,
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage,
        ExtractionContext context
    );
}