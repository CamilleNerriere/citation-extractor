package com.citationextractor.model;

import java.util.List;


public record TradCitationExtractionResult(List<Citation> citations, TroncatedCitation troncatedCitation) {

}
