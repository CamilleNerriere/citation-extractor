package com.citationextractor.utils;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

public interface IFontStats {
    float getAverageFontSize(List<TextPosition> positions);
    float getMedianSize(List<TextPosition> positions);
}
