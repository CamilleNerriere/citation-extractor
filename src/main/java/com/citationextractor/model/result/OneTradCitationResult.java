package com.citationextractor.model.result;

import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.TroncatedCitation;

public record OneTradCitationResult(Citation citation, TroncatedCitation trunc) {
    public boolean citationIsEmpty() {
        return citation.getText() == null || citation.getText().isEmpty();
    }
}
