package com.citationextractor.model;

public record OneTradCitationResult(Citation citation, TroncatedCitation trunc) {
    public boolean citationIsEmpty() {
        return citation.getText() == null || citation.getText().isEmpty();
    }
}
