package com.citationextractor.model;

public record OnePotentialCitationResult(Citation citation, TroncatedCitation trunc, int lastIndex) {
    public boolean citationIsEmpty() {
        return citation == null || citation.getText() == null || citation.getText().isEmpty();
    }
}
