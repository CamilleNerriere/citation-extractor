/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor;


/**
 *
 * @author camille
 */
public class AnnotatedCitation {
    private final Citation baseCitation;
    private final String noteNumberAString;

    public AnnotatedCitation(final Citation baseCitation, final String noteNumberAString){
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
