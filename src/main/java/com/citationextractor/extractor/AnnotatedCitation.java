/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor;

import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author camille
 */
public class AnnotatedCitation {
    private final Citation baseCitation;
    private final TextPosition noteChar;

    public AnnotatedCitation(final Citation baseCitation, final TextPosition noteChar){
        this.baseCitation = baseCitation;
        this.noteChar = noteChar;
    }

    public Citation getBaseCitation(){
        return baseCitation;
    }

    public TextPosition getNoteChar(){
        return noteChar;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + baseCitation.getText() + '\'' +
                ", page=" + baseCitation.getPage() +
                ", coord=" + baseCitation.getCoord().toString() +
                ", note=" + noteChar + 
                '}';
    }
}
