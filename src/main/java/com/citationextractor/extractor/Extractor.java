package com.citationextractor.extractor;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.extractor.citation.harvard.IHarvardCitationExtractor;
import com.citationextractor.extractor.citation.trad.IFootnoteExtractor;
import com.citationextractor.extractor.citation.trad.ITradCitationAnnotator;
import com.citationextractor.extractor.citation.trad.ITradCitationExtractor;
import com.citationextractor.extractor.note.INoteDetector;
import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.footnote.Footnote;
import com.citationextractor.model.result.AllTypeCitationsResult;
import com.citationextractor.model.result.HarvardCitationExtractionResult;
import com.citationextractor.model.result.TradCitationExtractionResult;
import com.citationextractor.pdf.CustomTextStripper;
import com.citationextractor.utils.IFontStats;

public class Extractor {

    private final IFontStats fontStats;
    private final INoteDetector noteDetector;
    private final ITradCitationExtractor citationExtractor;
    private final ITradCitationAnnotator citationAnnotator;
    private final IHarvardCitationExtractor harvardExtractor;
    private final IFootnoteExtractor footnoteExtractor;

    public Extractor(final IFontStats fontStats, final INoteDetector noteDetector,
            final ITradCitationExtractor citationExtractor, final ITradCitationAnnotator citationAnnotator,
            final IHarvardCitationExtractor harvardExtractor, final IFootnoteExtractor footnoteExtractor) {
        this.fontStats = fontStats;
        this.noteDetector = noteDetector;
        this.citationExtractor = citationExtractor;
        this.citationAnnotator = citationAnnotator;
        this.harvardExtractor = harvardExtractor;
        this.footnoteExtractor = footnoteExtractor;
    }

    public AllTypeCitationsResult extractAll(PDDocument document) throws IOException {

        // CompletableFuture<LinkedHashMap<Integer, List<AnnotatedTradCitation>>>
        // tradFuture = CompletableFuture
        // .supplyAsync(() -> {
        // try {
        // return extractTradCitations(document);
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }
        // });

        // CompletableFuture<LinkedHashMap<Integer, List<AnnotatedHarvardCitation>>>
        // harvardFuture = CompletableFuture
        // .supplyAsync(() -> {
        // try {
        // return extractHarvardCitations(document);
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }
        // });

        // try {
        // LinkedHashMap<Integer, List<AnnotatedTradCitation>> tradCitations =
        // tradFuture.get();
        // LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations =
        // harvardFuture.get();
        // return new AllTypeCitationsResult(harvardCitations, tradCitations);

        // } catch (InterruptedException | ExecutionException e) {
        // throw new IOException("Error during parallel extraction", e);
        // }
        LinkedHashMap<Integer, List<AnnotatedTradCitation>> tradCitations = extractTradCitations(document);
        LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations = extractHarvardCitations(document);

        return new AllTypeCitationsResult(harvardCitations, tradCitations);

    }

    private LinkedHashMap<Integer, List<AnnotatedTradCitation>> extractTradCitations(PDDocument document)
            throws IOException {

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<AnnotatedTradCitation>> foundCitations = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<Footnote>> footnotesPerPage = new LinkedHashMap<>();

        TroncatedCitation troncatedCitationFromLastPage = new TroncatedCitation(null, null);

        for (int page = 1; page <= pageCount; page++) {
            System.out.println("'méthode globale" + page);

            CustomTextStripper stripper = new CustomTextStripper();
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

            TradCitationExtractionResult result = citationExtractor.extractCitationsPerPage(context,
                    troncatedCitationFromLastPage);
            citationsCandidatesPerPage.put(page, result.citations());
            troncatedCitationFromLastPage = result.troncatedCitation();

            // second : we get all small number that can be a note calls
            notesCandidatesPerPage.put(page, noteDetector.getNoteCandidates(context));
            // notesCandidatesPerPage.put(page, noteDetector.getNoteCandidates(context));

            // third : we match to eleminate text between quotation marks that is note
            // citation
            foundCitations.put(page, citationAnnotator.getAnnotatedCitations(citationsCandidatesPerPage,
                    notesCandidatesPerPage, context));

            // forth : we associate with footnote content
            footnotesPerPage.put(page, footnoteExtractor.getFootnotes(context, notesCandidatesPerPage));

            System.out.println(footnotesPerPage);

            // System.out.println(notesCandidatesPerPage);

        }
        return foundCitations;
    }

    private LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> extractHarvardCitations(PDDocument document)
            throws IOException {

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations = new LinkedHashMap<>();

        TroncatedCitation troncatedCitationFromLastPage = new TroncatedCitation(null, null);

        for (int page = 1; page <= pageCount; page++) {
            CustomTextStripper stripper = new CustomTextStripper();
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);

            // set extraction context
            List<TextPosition> positions = stripper.getTextPositions();
            float avgFontSize = fontStats.getAverageFontSize(positions);
            float medianFontSize = fontStats.getMedianSize(positions);
            ExtractionContext context = new ExtractionContext(positions, page, avgFontSize, medianFontSize);

            HarvardCitationExtractionResult harvardCitationsResult = harvardExtractor.extractCitationsPerPage(context,
                    troncatedCitationFromLastPage);
            harvardCitations.put(page, harvardCitationsResult.harvardCitations());
            troncatedCitationFromLastPage = harvardCitationsResult.troncatedCitation();
        }

        return harvardCitations;
    }

}
