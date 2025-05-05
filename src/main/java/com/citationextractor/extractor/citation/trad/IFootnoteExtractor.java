package com.citationextractor.extractor.citation.trad;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.footnote.Footnote;

public interface IFootnoteExtractor {

    List<Footnote> getFootnotes(
         ExtractionContext context,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage);
}
