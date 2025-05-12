package com.citationextractor.model.result;

import java.util.List;

import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.text.Line;

public record BlocExtractionResult(List<BlocCitation> citation, List<Line> lines) {

}
