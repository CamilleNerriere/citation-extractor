package com.citationextractor.utils;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;


public interface ICoordStats {
    float getMedianXLineBegining(PDDocument document) throws IOException;

    float getMedianXLineEnd(PDDocument document) throws IOException;
}
