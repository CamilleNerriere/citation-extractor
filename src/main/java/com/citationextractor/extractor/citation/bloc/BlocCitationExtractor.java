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

        StringBuilder updatedTroncated = troncatedCitationFromLastPage;

        List<TextPosition> positions = context.getPositions();

        List<Line> allLines = extractPageLines(positions);

        List<BlocCitation> blocCitations = getBlocCitations(allLines, context);

        System.out.println(blocCitations);
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

    private List<BlocCitation> getBlocCitations(List<Line> allLines, ExtractionContext context) {

        int page = context.getPage();
        float medianXLineBegining = context.getLineCoordStatsResult().medianXLineBegining();
        float medianXLineEnd = context.getLineCoordStatsResult().medianXLineEnd();

        float medianFontSize = context.getMedianFontSize();

        List<BlocCitation> blocCitations = new ArrayList<>();

        List<Line> blocCitationLines = new ArrayList<>();

        for (Line line : allLines) {
            float xStartLine = line.getXStart();
            float xEndLine = line.getXEnd();
            
            // il faut que je récupère la taille médiane des caractères sur la ligne 
            float lineMedianFontSize = line.getMedianFontSize();

            boolean isFootnote = lineMedianFontSize < 0.8 * medianFontSize;

            if (xStartLine > medianXLineBegining && xEndLine < medianXLineEnd && !isFootnote) {
                blocCitationLines.add(line);
            } else if (!blocCitationLines.isEmpty()) {
                StringBuilder text = new StringBuilder();
                TextPosition startPos = blocCitationLines.get(0).getStartPos();
                TextPosition endPos = blocCitationLines.get(blocCitationLines.size() -1).getEndPos();

                for(Line citationLine : blocCitationLines){
                    text.append(citationLine.getText());
                }

                BlocCitation blocCitation = new BlocCitation(text.toString(), page, startPos, endPos);
                blocCitations.add(blocCitation);

                blocCitationLines.clear();
            }

        }

        return blocCitations;
    }

}
