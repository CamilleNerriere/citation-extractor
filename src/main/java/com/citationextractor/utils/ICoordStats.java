package com.citationextractor.utils;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.citationextractor.model.result.LineCoordStatsResult;


public interface ICoordStats {
    LineCoordStatsResult getLineCoordStats(PDDocument document) throws IOException;
}
