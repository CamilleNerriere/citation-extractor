package com.citationextractor.model.citation;

public class AnnotatedTradCitation {
    private final TradCitation baseCitation;
    private final String noteNumberAString;

    public AnnotatedTradCitation(final TradCitation baseCitation, final String noteNumberAString){
        this.baseCitation = baseCitation;
        this.noteNumberAString = noteNumberAString;
    }

    public TradCitation getBaseCitation(){
        return baseCitation;
    }

    public String getNoteNumber(){
        return noteNumberAString;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + baseCitation.getText() + '\'' +
                ", page=" + baseCitation.getPage() +
                ", endX=" + baseCitation.getXEnd() +
                ", endY=" + baseCitation.getYEnd() +
                ", note=" + noteNumberAString + 
                '}';
    }
}
