/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor;

/**
 *
 * @author camille
 */
public class Citation {
    private final String text;
    private final int page;
    private final BoundingBox coord; 

    public Citation(final String text, final int page, final BoundingBox coord){
        this.text = text;
        this.page = page;
        this.coord = coord; 
    }

    public String getText() {
        return text;
    }

    public int getPage() {
        return page;
    }

    public BoundingBox getCoord() {
        return coord;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + text + '\'' +
                ", page=" + page +
                ", coord=" + coord.toString() +
                '}';
    }
}
