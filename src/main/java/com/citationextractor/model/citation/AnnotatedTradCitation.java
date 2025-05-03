package com.citationextractor.model.citation;

public class AnnotatedTradCitation {
    private final Citation baseCitation;
    private final String noteNumberAString;

    public AnnotatedTradCitation(final Citation baseCitation, final String noteNumberAString){
        this.baseCitation = baseCitation;
        this.noteNumberAString = noteNumberAString;
    }

    public Citation getBaseCitation(){
        return baseCitation;
    }

    public String getNoteNumberAString(){
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
