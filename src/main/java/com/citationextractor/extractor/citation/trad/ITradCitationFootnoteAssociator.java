package com.citationextractor.extractor.citation.trad;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.CitationWithNote;
import com.citationextractor.model.footnote.Footnote;


public interface ITradCitationFootnoteAssociator {
    public LinkedHashMap<Integer, List<CitationWithNote>> associateCitationWithFootnote(LinkedHashMap<Integer, List<AnnotatedTradCitation>> citations,
            LinkedHashMap<Integer, List<Footnote>> footnotesPerPage);
}
