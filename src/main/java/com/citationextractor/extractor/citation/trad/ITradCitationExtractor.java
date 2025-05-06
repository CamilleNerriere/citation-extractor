package com.citationextractor.extractor.citation.trad;

import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.result.TradCitationExtractionResult;

public interface ITradCitationExtractor {
    TradCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage);

}
