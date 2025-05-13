package com.citationextractor.extractor.citation.bloc;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedBlocCitation;
import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;

public interface IBlocCitationAnnotator {
    List<AnnotatedBlocCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<BlocCitation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context);
}
