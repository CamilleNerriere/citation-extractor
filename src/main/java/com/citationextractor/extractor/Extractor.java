/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author camille
 */
public class Extractor {

    public LinkedHashMap<Integer, List<Citation>> extractAll(PDDocument document) throws IOException {

        CustomTextStripper stripper = new CustomTextStripper();

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, Map<TextPosition, ArrayList<Float>>> notesCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<AnnotatedCitation>> foundCitations = new LinkedHashMap<>();

        for (int page = 1; page <= pageCount; page++) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);
            List<TextPosition> positions = stripper.getTextPositions();
            citationsCandidatesPerPage.put(page, extractCitationsPerPage(positions, page));

            notesCandidatesPerPage.put(page, getNoteCandidates(positions));

            foundCitations.put(page, getAnnotatedCitations(citationsCandidatesPerPage, notesCandidatesPerPage));
        }

        for (int page : citationsCandidatesPerPage.keySet()) {
            List<Citation> citations = citationsCandidatesPerPage.get(page);
            List<AnnotatedCitation> sortedCitations = new ArrayList<>();

            for (Citation citation : citations) {
                BoundingBox boundingBox = citation.getCoord();
                float xCitationEnd = boundingBox.getX() + boundingBox.getWidth();
                float yCitation = boundingBox.getY();

                Map<TextPosition, ArrayList<Float>> notesInPage = notesCandidatesPerPage.get(page);

                for (TextPosition note : notesInPage.keySet()) {
                    ArrayList<Float> coords = notesInPage.get(note);
                    Float xNote = coords.get(0);
                    Float yNote = coords.get(1);

                    float dx = xNote - xCitationEnd;
                    float dy = Math.abs(yNote - yCitation);

                    if(dx >= -10 && dx < 100 && dy < 25) {
                        AnnotatedCitation annotatedCitation = new AnnotatedCitation(citation, note);
                        sortedCitations.add(annotatedCitation);
                    } 
                }
            }
            foundCitations.put(page, sortedCitations);
        }

        System.out.println(foundCitations);

        return citationsCandidatesPerPage;
    }

    private List<Citation> extractCitationsPerPage(List<TextPosition> positions, int page) {

        List<Citation> citations = new ArrayList<>();

        String[] openingQuotes = { "«", "\"", "“" };

        for (String opening : openingQuotes) {
            citations.addAll(extractCitations(positions, opening, page));
        }

        return citations;
    }

    private List<Citation> extractCitations(List<TextPosition> positions, String c1, int page) {
        List<Citation> citations = new ArrayList<>();

        String c2 = switch (c1) {
            case "«" -> c2 = "»";
            case "\"" -> c2 = "\"";
            case "“" -> c2 = "”";
            default -> throw new IllegalArgumentException("Unauthorized Opening Quote" + c1);
        };

        for (int i = 0; i < positions.size(); i++) {
            int start;
            StringBuilder builder = new StringBuilder("");

            if (positions.get(i).getUnicode().equals(c1)) {

                start = i;

                // Get citation x,y, width, height
                float xStart = positions.get(i).getXDirAdj();
                float y = positions.get(i).getYDirAdj();
                float width;
                float height = positions.get(i).getHeightDir();

                for (int j = start; j < positions.size(); j++) {
                    if (!positions.get(j).getUnicode().equals(c2)) {
                        if (!positions.get(j).getUnicode().equals(c1)) {
                            builder.append(positions.get(j).getUnicode());
                        }
                    } else {
                        float xEnd = positions.get(j).getXDirAdj() + positions.get(j).getWidthDirAdj();
                        width = xEnd - xStart;
                        Citation citation = new Citation(builder.toString().trim(), page,
                                new BoundingBox(xStart, y, width, height));
                        citations.add(citation);
                        break;
                    }
                }
            }
        }
        return citations;
    }

    private Map<TextPosition, ArrayList<Float>> getNoteCandidates(List<TextPosition> positions) {

        float averageFontSize = getAverageFontSize(positions);

        Map<TextPosition, ArrayList<Float>> notesWithPositions = new LinkedHashMap<>();

        for (TextPosition candidate : positions) {
            String c = candidate.getUnicode();

            char ch = c.charAt(0);
            int type = Character.getType(ch);
            float deltaX = candidate.getXDirAdj();
            float deltaY = candidate.getYDirAdj();

            // Doit être un chiffre (chiffre décimal ou caractère comme ¹)
            if ((type == Character.DECIMAL_DIGIT_NUMBER || type == Character.OTHER_NUMBER)
                    && candidate.getFontSizeInPt() < averageFontSize * 0.75) {
                ArrayList<Float> deltas = new ArrayList<>();
                deltas.add(deltaX);
                deltas.add(deltaY);
                notesWithPositions.put(candidate, deltas);
            }
        }

        return notesWithPositions;
    }

    private float getAverageFontSize(List<TextPosition> positions) {

        Map<Float, Integer> sizeStatistics = new LinkedHashMap<>();

        for (TextPosition tp : positions) {
            String c = tp.getUnicode();
            if (!c.trim().isEmpty()) {
                float size = tp.getFontSizeInPt();

                if (sizeStatistics.containsKey(size)) {
                    sizeStatistics.put(size, sizeStatistics.get(size) + 1);
                } else {
                    sizeStatistics.put(size, 1);
                }
            }
        }

        float averageSize = 0f;
        int frequence = 0;

        for (Float size : sizeStatistics.keySet()) {
            Integer value = sizeStatistics.get(size);
            if (value > frequence) {
                averageSize = size;
                frequence = value;
            }
        }

        return averageSize;
    }

    private List<AnnotatedCitation> getAnnotatedCitations(LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage, LinkedHashMap<Integer, Map<TextPosition, ArrayList<Float>>> notesCandidatesPerPage){   
        List<AnnotatedCitation> sortedCitations = new ArrayList<>();
        for (int page : citationsCandidatesPerPage.keySet()) {
            List<Citation> citations = citationsCandidatesPerPage.get(page);

            for (Citation citation : citations) {
                BoundingBox boundingBox = citation.getCoord();
                float xCitationEnd = boundingBox.getX() + boundingBox.getWidth();
                float yCitation = boundingBox.getY();

                Map<TextPosition, ArrayList<Float>> notesInPage = notesCandidatesPerPage.get(page);

                for (TextPosition note : notesInPage.keySet()) {
                    ArrayList<Float> coords = notesInPage.get(note);
                    Float xNote = coords.get(0);
                    Float yNote = coords.get(1);

                    float dx = xNote - xCitationEnd;
                    float dy = Math.abs(yNote - yCitation);

                    if(dx >= -10 && dx < 100 && dy < 25) {
                        AnnotatedCitation annotatedCitation = new AnnotatedCitation(citation, note);
                        sortedCitations.add(annotatedCitation);
                    } 
                }
            }
        }
        return sortedCitations;
    }

}
