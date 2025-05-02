package com.citationextractor.model;

public record TroncatedCitation(String content, String openingQuote) {
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}
