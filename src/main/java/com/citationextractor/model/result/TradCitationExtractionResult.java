package com.citationextractor.model.result;

import java.util.List;

import com.citationextractor.model.citation.TradCitation;
import com.citationextractor.model.citation.TroncatedCitation;


public record TradCitationExtractionResult(List<TradCitation> citations, TroncatedCitation troncatedCitation) {

}
