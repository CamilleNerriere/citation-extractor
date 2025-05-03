package com.citationextractor.extractor.citation;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.AnnotatedHarvardCitation;
import com.citationextractor.model.Citation;
import com.citationextractor.model.HarvardCitationExtractionResult;
import com.citationextractor.model.OnePotentialCitationResult;
import com.citationextractor.model.TroncatedCitation;

public interface IHarvardCitationExtractor {
        HarvardCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
                        TroncatedCitation troncatedCitationFromLastPage);

        HarvardCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
                        TroncatedCitation troncatedCitationFromLastPage);

        OnePotentialCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
                        String remainingTextFromLastPage, int start);

        AnnotatedHarvardCitation extractOneHarvardCitation(ExtractionContext context, Citation citation,
                        int start);
                        
}
