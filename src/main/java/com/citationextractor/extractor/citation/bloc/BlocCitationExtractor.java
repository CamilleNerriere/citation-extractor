package com.citationextractor.extractor.citation.bloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.text.Line;
import com.citationextractor.utils.FontStats;

public class BlocCitationExtractor implements IBlocCitationExtractor {
    @Override
    public void extractCitationsPerPage(ExtractionContext context,
            StringBuilder troncatedCitationFromLastPage) {

        List<BlocCitation> blocCitations = new ArrayList<>();
        StringBuilder updatedTroncated = troncatedCitationFromLastPage;

        int page = context.getPage();
        List<TextPosition> positions = context.getPositions();
        float medianXLineBegining = context.getLineCoordStatsResult().medianXLineBegining();
        float medianXLineEnd = context.getLineCoordStatsResult().medianXLineEnd();

        // récupérer le texte ligne par ligne

        List<Line> allLines = extractPageLines(positions);

        System.out.println(allLines);
    }

    private List<Line> extractPageLines(List<TextPosition> positions) {

        List<Line> allLines = new ArrayList<>();

        if (positions.isEmpty())
            return allLines;

        TextPosition firstPosInLine = positions.get(0);

        StringBuilder text = new StringBuilder();

        List<TextPosition> linePositions = new ArrayList<>();

        int lineNumber = 1;

        for (int i = 0; i < positions.size(); i++) {
            TextPosition lastPosition = i > 0 ? positions.get(i - 1) : null;
            TextPosition actualPosition = positions.get(i);

            float lastPosX = lastPosition != null ? lastPosition.getXDirAdj() : actualPosition.getXDirAdj();
            float actualPosX = actualPosition.getXDirAdj();

            float xDiff = actualPosX - lastPosX;

            String actualChar = actualPosition.getUnicode();

            if (xDiff < -20f) {
                FontStats stats = new FontStats();
                Line line = new Line(lineNumber, text.toString(), firstPosInLine, positions.get(i - 1),
                        stats.getMedianSize(linePositions));
                allLines.add(line);
                text.setLength(0);
                firstPosInLine = positions.get(i);
                linePositions.clear();
            }

            text.append(actualChar);
            linePositions.add(actualPosition);
        }

        if (!linePositions.isEmpty()) {
            FontStats stats = new FontStats();
            Line line = new Line(lineNumber, text.toString(), firstPosInLine,
                    linePositions.get(linePositions.size() - 1),
                    stats.getMedianSize(linePositions));
            allLines.add(line);
        }

        return allLines;
    }
}
