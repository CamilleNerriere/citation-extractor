package com.citationextractor.model.result;

import com.citationextractor.model.citation.BlocCitation;

public record BlocCitationExtractionResult(BlocCitation citations, StringBuilder troncatedCitation) {

}
