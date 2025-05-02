/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.citationextractor.extractor.citation;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.CitationExtractionResult;
import com.citationextractor.model.OneCitationResult;
import com.citationextractor.model.TroncatedCitation;

/**
 *
 * @author camille
 */
public interface ICitationExtractor {
    CitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage);

    CitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage);

    OneCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start);
}
