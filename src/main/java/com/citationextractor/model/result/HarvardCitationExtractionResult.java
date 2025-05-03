package com.citationextractor.model.result;

import java.util.List;

import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.TroncatedCitation;


public record HarvardCitationExtractionResult(List<AnnotatedHarvardCitation> harvardCitations, TroncatedCitation troncatedCitation) {

}
