package com.citationextractor.extractor.citation.trad;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.Citation;
import com.citationextractor.model.OneTradCitationResult;
import com.citationextractor.model.TradCitationExtractionResult;
import com.citationextractor.model.TroncatedCitation;

public class TradCitationExtractor implements ITradCitationExtractor {
    
    @Override
    public TradCitationExtractionResult extractCitationsPerPage(ExtractionContext context,
            TroncatedCitation troncatedCitationFromLastPage) {

        List<Citation> allCitations = new ArrayList<>();
        TroncatedCitation updatedTroncated = troncatedCitationFromLastPage;

        String[] openingQuotes = { "«", "\"", "“" };

        for (String opening : openingQuotes) {
            String content = (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote()))
                    ? updatedTroncated.content()
                    : null;
            TroncatedCitation troncatedToPass = new TroncatedCitation(content, opening);
            TradCitationExtractionResult result = extractCitations(context, opening, troncatedToPass);
            allCitations.addAll(result.citations());

            if (result.troncatedCitation().isEmpty() == false) {
                updatedTroncated = result.troncatedCitation();
            } else if (updatedTroncated != null && opening.equals(updatedTroncated.openingQuote())) {
                updatedTroncated = new TroncatedCitation(null, null);

            }
        }

        return new TradCitationExtractionResult(allCitations, updatedTroncated);
    }

    @Override
    public TradCitationExtractionResult extractCitations(ExtractionContext context, String openingQuote,
            TroncatedCitation troncatedCitationFromLastPage) {

        List<Citation> citations = new ArrayList<>();

        String truncContent = troncatedCitationFromLastPage.content();
        String truncOpeningQuote = troncatedCitationFromLastPage.openingQuote();

        if (!troncatedCitationFromLastPage.isEmpty()) {
            OneTradCitationResult citationResult = extractOneCitation(context,
                    truncOpeningQuote,
                    truncContent, 0);

            if (citationResult.citation() != null) {
                citations.add(citationResult.citation());
                truncContent = "";
            } else {
                truncContent = citationResult.trunc().content();
                truncOpeningQuote = citationResult.trunc().openingQuote();
            }

        }

        List<TextPosition> positions = context.getPositions();

        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getUnicode().equals(openingQuote)) {
                OneTradCitationResult citationResult = extractOneCitation(context, openingQuote, "", i);
                if (citationResult.citation() != null) {
                    citations.add(citationResult.citation());
                    truncContent = "";
                } else {
                    truncContent = citationResult.trunc().content();
                    truncOpeningQuote = citationResult.trunc().openingQuote();
                }
            }
        }

        return new TradCitationExtractionResult(citations, new TroncatedCitation(truncContent, truncOpeningQuote));
    }

    @Override
    public OneTradCitationResult extractOneCitation(ExtractionContext context, String openingQuote,
            String remainingTextFromLastPage, int start) {


        String c1 = openingQuote;
        StringBuilder citationContent = new StringBuilder(remainingTextFromLastPage);
        Boolean isClosed = false;

        String c2 = switch (c1) {
            case "«" -> "»";
            case "\"" -> "\"";
            case "“" -> "”";
            default -> throw new IllegalArgumentException("Unauthorized Opening Quote" + c1);
        };

        // Get first and last char to calculate note positions

        List<TextPosition> positions = context.getPositions();
        float medianFontSize = context.getMedianFontSize();

        TextPosition firstChar = positions.get(start);
        TextPosition lastChar = null;

        for (int j = start; j < positions.size(); j++) {

            TextPosition currentCharAsPosition = positions.get(j);
            String currentChar = currentCharAsPosition.getUnicode();

            if (!currentChar.equals(c2)) {

                if (currentCharAsPosition.getFontSizeInPt() < medianFontSize * 0.95) {
                    continue;
                }

                if (!currentChar.equals(c1)) {
                    citationContent.append(positions.get(j).getUnicode());
                }
            } else {
                lastChar = positions.get(j);
                isClosed = true;
                break;

            }
        }

        if (isClosed) {
            Citation citation = new Citation(citationContent.toString().trim(), context.getPage(),
                    firstChar, lastChar, c1);
            TroncatedCitation trunc = new TroncatedCitation(null, null);
            return new OneTradCitationResult(citation, trunc);
        }

        return new OneTradCitationResult(null, new TroncatedCitation(citationContent.toString(), openingQuote));

    }
}
