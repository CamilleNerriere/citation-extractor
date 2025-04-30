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
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<AnnotatedCitation>> foundCitations = new LinkedHashMap<>();

        for (int page = 1; page <= pageCount; page++) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);
            List<TextPosition> positions = stripper.getTextPositions();
            citationsCandidatesPerPage.put(page, extractCitationsPerPage(positions, page));

            notesCandidatesPerPage.put(page, getNoteCandidates(positions, page));

            foundCitations.put(page, getAnnotatedCitations(citationsCandidatesPerPage,
                    notesCandidatesPerPage, page));
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

                // Get first and last char to calculate note positions

                TextPosition firstChar = positions.get(i);
                TextPosition lastChar;

                for (int j = start; j < positions.size(); j++) {
                    if (!positions.get(j).getUnicode().equals(c2)) {
                        if (!positions.get(j).getUnicode().equals(c1)) {
                            builder.append(positions.get(j).getUnicode());
                        }
                    } else {
                        lastChar = positions.get(j);
                        Citation citation = new Citation(builder.toString().trim(), page,
                                firstChar, lastChar);
                        citations.add(citation);
                        break;
                    }
                }
            }
        }
        return citations;
    }

    private List<NoteCandidate> getNoteCandidates(List<TextPosition> positions, int page) {

        float averageFontSize = getAverageFontSize(positions);

        List<NoteCandidate> noteCandidates = new ArrayList<>();

        TextPosition lastNumberFound = null;
        StringBuilder completeNote = new StringBuilder("");
        float xStart = 0;
        float yStart = 0;

        for (TextPosition candidate : positions) {
            String c = candidate.getUnicode();
            if (c == null || c.length() == 0)
                continue;

            char ch = c.charAt(0);
            int type = Character.getType(ch);

            // Doit être un chiffre (chiffre décimal ou caractère comme ¹)
            if ((type == Character.DECIMAL_DIGIT_NUMBER || type == Character.OTHER_NUMBER)
                    && candidate.getFontSizeInPt() < averageFontSize * 0.75) {

                // a previous number has been found
                if (lastNumberFound != null) {

                    float deltaX = Math.abs(candidate.getXDirAdj() - lastNumberFound.getXDirAdj());
                    float deltaY = Math.abs(candidate.getYDirAdj() - lastNumberFound.getYDirAdj());

                    if (deltaX < 10 && deltaY < 10) { // it's close
                        completeNote.append(candidate.getUnicode());
                    } else { // it's not close -> start a new note
                        if (completeNote.length() > 0) {
                            noteCandidates.add(new NoteCandidate(completeNote.toString(), page, xStart, yStart));
                        }
                        completeNote.setLength(0);
                        completeNote.append(candidate.getUnicode());
                        xStart = candidate.getXDirAdj();
                        yStart = candidate.getYDirAdj();
                    }
                } else { // complete note is empty -> start a new note
                    completeNote.setLength(0);
                    completeNote.append(candidate.getUnicode());
                    xStart = candidate.getXDirAdj();
                    yStart = candidate.getYDirAdj();
                }

                lastNumberFound = candidate;
            }

        }

        if (completeNote.length() > 0) {
            noteCandidates.add(new NoteCandidate(completeNote.toString(), page, xStart, yStart));
        }

        return noteCandidates;
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

    private List<AnnotatedCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, int page) {

        List<AnnotatedCitation> sortedCitations = new ArrayList<>();

        List<Citation> citations = citationsCandidatesPerPage.get(page);

        for (Citation citation : citations) {

            float xCitationEnd = citation.getXEnd();
            float yCitation = citation.getYEnd();

            List<NoteCandidate> notesInPage = notesCandidatesPerPage.get(page);

            for (NoteCandidate note : notesInPage) {
                Float xNote = note.getX();
                Float yNote = note.getY();

                float dx = xNote - xCitationEnd;
                float dy = Math.abs(yNote - yCitation);

                if (dx >= 0 && dx < 20 && dy < 25) {
                    AnnotatedCitation annotatedCitation = new AnnotatedCitation(citation, note.getText());
                    sortedCitations.add(annotatedCitation);
                }
            }

        }
        return sortedCitations;
    }

}
