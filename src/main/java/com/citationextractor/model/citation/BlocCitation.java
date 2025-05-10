package com.citationextractor.model.citation;

import org.apache.pdfbox.text.TextPosition;

public class BlocCitation {
    private final String text;
    private final int page;
    private final TextPosition startPos; // first char position
    private final TextPosition endPos; // last char lposition
    private final String openingQuote;
    private final Float leftMargin; // we extract the length of the margin to check if bloc citation
    private final Float rightMargin;

    public BlocCitation(final String text, final int page, final TextPosition startPos, final TextPosition endPos,
            final String openingQuote, final Float leftMargin, final Float rightMargin) {
        this.text = text;
        this.page = page;
        this.startPos = startPos;
        this.endPos = endPos;
        this.openingQuote = openingQuote;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
    }

    public String getText() {
        return text;
    }

    public int getPage() {
        return page;
    }

    public TextPosition getStartPos() {
        return startPos;
    }

    public TextPosition getEndPos() {
        return endPos;
    }

    public float getXEnd() {
        return endPos.getXDirAdj() + endPos.getWidthDirAdj();
    }

    public float getYEnd() {
        return endPos.getYDirAdj();
    }

    public String getOpeningQuote() {
        return openingQuote;
    }

    public Float getLeftMargin() {
        return leftMargin;
    }

    public Float getRightMargin() {
        return rightMargin;
    }

    @Override
    public String toString() {
        return "Citation{" +
                "text='" + text + '\'' +
                ", page=" + page +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", openingQuote=" + openingQuote +
                ", leftMargin=" + leftMargin +
                ", rightMargin=" + rightMargin +
                '}';
    }
}
