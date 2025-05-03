package com.citationextractor.extractor.citation.trad;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.OneTradCitationResult;
import com.citationextractor.model.TradCitationExtractionResult;
import com.citationextractor.model.TroncatedCitation;

public interface ITradCitationExtractor {
    TradCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage);

    TradCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage);

    OneTradCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start);
}
