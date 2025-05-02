package com.citationextractor.extractor;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.extractor.citation.ICitationAnnotator;
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


public class Extractor {

    private final IFontStats fontStats;
    private final INoteDetector noteDetector;
    private final ICitationExtractor citationExtractor;
    private final ICitationAnnotator citationAnnotator;

    public Extractor(final IFontStats fontStats, final INoteDetector noteDetector, final ICitationExtractor citationExtractor, final ICitationAnnotator citationAnnotator){
        this.fontStats = fontStats;
        this.noteDetector = noteDetector;
        this.citationExtractor = citationExtractor;
        this.citationAnnotator = citationAnnotator;
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
            notesCandidatesPerPage.put(page, noteDetector.getNoteCandidates(context));

            // third : we match to eleminate text between quotation marks that is note
            // citation
            foundCitations.put(page, citationAnnotator.getAnnotatedCitations(citationsCandidatesPerPage,
                    notesCandidatesPerPage, context));
        }

        // System.out.println(foundCitations);
        return citationsCandidatesPerPage;
    }

}
