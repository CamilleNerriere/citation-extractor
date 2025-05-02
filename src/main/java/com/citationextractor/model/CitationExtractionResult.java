package com.citationextractor.model;

import java.util.List;


public record CitationExtractionResult(List<Citation> citations, TroncatedCitation troncatedCitation) {

}
