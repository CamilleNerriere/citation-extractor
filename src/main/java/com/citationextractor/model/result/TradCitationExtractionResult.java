package com.citationextractor.model.result;

import java.util.List;

import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.TroncatedCitation;


public record TradCitationExtractionResult(List<Citation> citations, TroncatedCitation troncatedCitation) {

}
