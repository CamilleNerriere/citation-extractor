package com.citationextractor.model.result;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.AnnotatedTradCitation;

public record AllTypeCitationsResult(LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations, LinkedHashMap<Integer, List<AnnotatedTradCitation>> tradCitations) {

}
