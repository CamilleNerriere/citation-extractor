package com.citationextractor.extractor.citation.trad;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;

public interface ITradCitationAnnotator {
    List<AnnotatedTradCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context);
}
