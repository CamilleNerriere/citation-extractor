package com.citationextractor.model.result;

import com.citationextractor.model.citation.AnnotatedHarvardCitation;

public record HarvardExtractionResult(AnnotatedHarvardCitation citation, String truncContent, String truncOpeningQuote) {}


