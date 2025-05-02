/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.extractor.citation.ICitationExtractor;
import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.extractor.note.INoteDetector;
import com.citationextractor.model.AnnotatedCitation;
import com.citationextractor.model.Citation;
import com.citationextractor.model.CitationExtractionResult;
import com.citationextractor.model.NoteCandidate;
import com.citationextractor.model.TroncatedCitation;
import com.citationextractor.pdf.CustomTextStripper;
import com.citationextractor.utils.IFontStats;

/**
 *
 * @author camille
 */
public class Extractor {

    private final IFontStats fontStats;
    private final INoteDetector noteDetector;
    private final ICitationExtractor citationExtractor;

    public Extractor(final IFontStats fontStats, final INoteDetector noteDetector, final ICitationExtractor citationExtractor){
        this.fontStats = fontStats;
        this.noteDetector = noteDetector;
        this.citationExtractor = citationExtractor;
    }

    public LinkedHashMap<Integer, List<Citation>> extractAll(PDDocument document) throws IOException {

        CustomTextStripper stripper = new CustomTextStripper();

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<AnnotatedCitation>> foundCitations = new LinkedHashMap<>();

        TroncatedCitation troncatedCitationFromLastPage = new TroncatedCitation(null, null);

        for (int page = 1; page <= pageCount; page++) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);

            // set extraction context 
            List<TextPosition> positions = stripper.getTextPositions();
            float avgFontSize = fontStats.getAverageFontSize(positions);
            float medianFontSize = fontStats.getMedianSize(positions);
            ExtractionContext context = new ExtractionContext(positions, page, avgFontSize, medianFontSize);

            // first : we get everything that's between quotation marks

            CitationExtractionResult result = citationExtractor.extractCitationsPerPage(context, troncatedCitationFromLastPage);
            citationsCandidatesPerPage.put(page, result.citations());
            troncatedCitationFromLastPage = result.troncatedCitation();

            // second : we get all small number that can be a note calls
            notesCandidatesPerPage.put(page, noteDetector.getNoteCandidates(positions, page, avgFontSize));

            // third : we match to eleminate text between quotation marks that is note
            // citation
            foundCitations.put(page, getAnnotatedCitations(citationsCandidatesPerPage,
                    notesCandidatesPerPage, page));
        }

        System.out.println(foundCitations);
        return citationsCandidatesPerPage;
    }

    // private CitationExtractionResult extractCitationsPerPage(List<TextPosition> positions,
    //         TroncatedCitation troncatedCitationFromLastPage, int page) {

    //     List<Citation> allCitations = new ArrayList<>();
    //     TroncatedCitation updatedTroncated = troncatedCitationFromLastPage;

    //     String[] openingQuotes = { "«", "\"", "“" };

    //     for (String opening : openingQuotes) {
    //         String content = (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote()))
    //                 ? updatedTroncated.content()
    //                 : null;
    //         TroncatedCitation troncatedToPass = new TroncatedCitation(content, opening);
    //         CitationExtractionResult result = extractCitations(positions, opening, troncatedToPass, page);
    //         allCitations.addAll(result.citations());

    //         if (result.troncatedCitation().isEmpty() == false) {
    //             updatedTroncated = result.troncatedCitation();
    //         } else if (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote())) {
    //             updatedTroncated = new TroncatedCitation(null, null);

    //         }
    //     }

    //     return new CitationExtractionResult(allCitations, updatedTroncated);
    // }

    // private CitationExtractionResult extractCitations(List<TextPosition> positions, String openingQuote,
    //         TroncatedCitation troncatedCitationFromLastPage, int page) {

    //     List<Citation> citations = new ArrayList<>();

    //     String truncContent = troncatedCitationFromLastPage.content();
    //     String truncOpeningQuote = troncatedCitationFromLastPage.openingQuote();

    //     if (!troncatedCitationFromLastPage.isEmpty()) {
    //         OneCitationResult citationResult = extractOneCitation(positions,
    //                 troncatedCitationFromLastPage.openingQuote(),
    //                 troncatedCitationFromLastPage.content(), page, 0);

    //         if (citationResult.citation() != null) {
    //             citations.add(citationResult.citation());
    //             truncContent = "";
    //         } else {
    //             truncContent = citationResult.trunc().content();
    //             truncOpeningQuote = citationResult.trunc().openingQuote();
    //         }

    //     }

    //     for (int i = 0; i < positions.size(); i++) {
    //         if (positions.get(i).getUnicode().equals(openingQuote)) {
    //             OneCitationResult citationResult = extractOneCitation(positions, openingQuote, "", page, i);
    //             if (citationResult.citation() != null) {
    //                 citations.add(citationResult.citation());
    //                 truncContent = "";
    //             } else {
    //                 truncContent = citationResult.trunc().content();
    //                 truncOpeningQuote = citationResult.trunc().openingQuote();
    //             }
    //         }
    //     }

    //     return new CitationExtractionResult(citations, new TroncatedCitation(truncContent, truncOpeningQuote));
    // }

    // private OneCitationResult extractOneCitation(List<TextPosition> positions, String openingQuote,
    //         String remainingTextFromLastPage, int page, int start) {

    //     Float medianeFontSize = fontStats.getMedianeSize(positions);

    //     String c1 = openingQuote;
    //     StringBuilder citationContent = new StringBuilder(remainingTextFromLastPage);
    //     Boolean isClosed = false;

    //     String c2 = switch (c1) {
    //         case "«" -> "»";
    //         case "\"" -> "\"";
    //         case "“" -> "”";
    //         default -> throw new IllegalArgumentException("Unauthorized Opening Quote" + c1);
    //     };

    //     // Get first and last char to calculate note positions

    //     TextPosition firstChar = positions.get(start);
    //     TextPosition lastChar = null;

    //     for (int j = start; j < positions.size(); j++) {

    //         TextPosition currentCharAsPosition = positions.get(j);
    //         String currentChar = currentCharAsPosition.getUnicode();

    //         if (!currentChar.equals(c2)) {

    //             if (currentCharAsPosition.getFontSizeInPt() < medianeFontSize * 0.95) {
    //                 continue;
    //             }

    //             if (!currentChar.equals(c1)) {
    //                 citationContent.append(positions.get(j).getUnicode());
    //             }
    //         } else {
    //             lastChar = positions.get(j);
    //             isClosed = true;
    //             break;

    //         }
    //     }

    //     if (isClosed) {
    //         Citation citation = new Citation(citationContent.toString().trim(), page,
    //                 firstChar, lastChar, c1);
    //         TroncatedCitation trunc = new TroncatedCitation(null, null);
    //         return new OneCitationResult(citation, trunc);
    //     }

    //     return new OneCitationResult(null, new TroncatedCitation(citationContent.toString(), openingQuote));

    // }


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

                if (dx >= -1 && dx < 20 && dy < 25) {
                    AnnotatedCitation annotatedCitation = new AnnotatedCitation(citation, note.getText());
                    sortedCitations.add(annotatedCitation);
                }
            }

        }
        return sortedCitations;
    }

}
