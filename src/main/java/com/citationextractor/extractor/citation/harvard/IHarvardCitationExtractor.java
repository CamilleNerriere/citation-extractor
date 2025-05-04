package com.citationextractor.extractor.citation.harvard;

import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.result.HarvardCitationExtractionResult;
import com.citationextractor.model.result.HarvardExtractionResult;
import com.citationextractor.model.result.OnePotentialCitationResult;

public interface IHarvardCitationExtractor {
        HarvardCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
                        TroncatedCitation troncatedCitationFromLastPage);

        HarvardCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
                        TroncatedCitation troncatedCitationFromLastPage);

        OnePotentialCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
                        String remainingTextFromLastPage, int start);

        AnnotatedHarvardCitation extractOneHarvardCitation(ExtractionContext context, Citation citation,
                        int start);

        HarvardExtractionResult getExtractionResult(ExtractionContext context, OnePotentialCitationResult citationResult);
}
