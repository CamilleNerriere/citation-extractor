package com.citationextractor.extractor.citation;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.CitationExtractionResult;
import com.citationextractor.model.OneCitationResult;
import com.citationextractor.model.TroncatedCitation;

public interface ICitationExtractor {
    CitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage);

    CitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage);

    OneCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start);
}
