package com.citationextractor.model.result;

import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.TroncatedCitation;

public record OnePotentialCitationResult(Citation citation, TroncatedCitation trunc, int lastIndex) {
    public boolean citationIsEmpty() {
        return citation == null || citation.getText() == null || citation.getText().isEmpty();
    }
}
