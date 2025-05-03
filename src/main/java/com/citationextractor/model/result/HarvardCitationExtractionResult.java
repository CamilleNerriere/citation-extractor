package com.citationextractor.model;

import java.util.List;


public record HarvardCitationExtractionResult(List<AnnotatedHarvardCitation> harvardCitations, TroncatedCitation troncatedCitation) {

}
