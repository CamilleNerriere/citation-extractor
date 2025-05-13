package com.citationextractor.model.result;

import com.citationextractor.model.citation.TradCitation;
import com.citationextractor.model.citation.TroncatedCitation;

public record OneTradCitationResult(TradCitation citation, TroncatedCitation trunc) {
    public boolean citationIsEmpty() {
        return citation.getText() == null || citation.getText().isEmpty();
    }
}
