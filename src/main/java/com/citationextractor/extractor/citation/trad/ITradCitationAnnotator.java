package com.citationextractor.extractor.citation.trad;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.AnnotatedTradCitation;
import com.citationextractor.model.Citation;
import com.citationextractor.model.NoteCandidate;

public interface ITradCitationAnnotator {
    List<AnnotatedTradCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context);
}
