package com.citationextractor.model.text;

import org.apache.pdfbox.text.TextPosition;

public class Line {

    private final int number;
    private final String text;
    private final TextPosition startPos;
    private final TextPosition endPos;
    private final float medianFontSize;

    public Line(int number, String text, TextPosition startPos, TextPosition endPos, float medianFontSize) {
        this.number = number;
        this.text = text;
        this.startPos = startPos;
        this.endPos = endPos;
        this.medianFontSize = medianFontSize;
    }

    public int getLineNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public TextPosition getStartPos() {
        return startPos;
    }

    public TextPosition getEndPos() {
        return endPos;
    }

    public float getXStart() {
        return startPos.getXDirAdj();
    }

    public float getYStart() {
        return startPos.getYDirAdj();
    }

    public float getXEnd() {
        return endPos.getXDirAdj() + endPos.getWidthDirAdj();
    }

    public float getYEnd() {
        return endPos.getYDirAdj();
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + text + '\'' +
                ", number=" + number +
                ", xStart=" + getXStart() +
                ", YStart=" + getYStart() +
                ", xEnd=" + getXEnd() +
                ", YEnd=" + getYEnd() +
                '}';
    }
}
