package com.citationextractor.extractor.citation.trad;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.result.OneTradCitationResult;
import com.citationextractor.model.result.TradCitationExtractionResult;

public interface ITradCitationExtractor {
    TradCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage);

    TradCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage);

    OneTradCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start);
}
