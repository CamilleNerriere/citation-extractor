/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor.context;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author camille
 */
public class ExtractionContext {
    private final List<TextPosition> positions;
    private final int page;
    private final float averageFontSize;
    private final float medianFontSize;

    public ExtractionContext(final List<TextPosition> positions, final int page, final float averageFontSize, final float medianFontSize) {
        this.positions = positions;
        this.page = page;
        this.averageFontSize = averageFontSize;
        this.medianFontSize = medianFontSize;
    }

    public List<TextPosition> getPositions() {
        return positions;
    }

    public int getPage() {
        return page;
    }

    public float getAverageFontSize() {
        return averageFontSize;
    }

    public float getMedianFontSize() {
        return medianFontSize;
    }

}
