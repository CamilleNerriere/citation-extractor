package com.citationextractor.extractor.citation.harvard;

import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.result.HarvardCitationExtractionResult;


public interface IHarvardCitationExtractor {
        HarvardCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
                        TroncatedCitation troncatedCitationFromLastPage);
}
