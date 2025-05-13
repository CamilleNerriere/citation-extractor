package com.citationextractor.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citationextractor.annotator.ICitationAnnotator;
import com.citationextractor.extractor.citation.bloc.IBlocCitationExtractor;
import com.citationextractor.extractor.citation.harvard.IHarvardCitationExtractor;
import com.citationextractor.extractor.citation.trad.ITradCitationExtractor;
import com.citationextractor.extractor.citation.trad.ITradCitationFootnoteAssociator;
import com.citationextractor.extractor.footnote.IFootnoteExtractor;
import com.citationextractor.extractor.note.INoteDetector;
import com.citationextractor.model.citation.AnnotatedBlocCitation;
import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.CitationWithNote;
import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.footnote.Footnote;
import com.citationextractor.model.result.AllTypeCitationsResult;
import com.citationextractor.model.result.BlocExtractionResult;
import com.citationextractor.model.result.HarvardCitationExtractionResult;
import com.citationextractor.model.result.LineCoordStatsResult;
import com.citationextractor.model.result.TradCitationExtractionResult;
import com.citationextractor.model.text.Line;
import com.citationextractor.pdf.CustomTextStripper;
import com.citationextractor.utils.ICoordStats;
import com.citationextractor.utils.IFontStats;

public class Extractor {

    private final IFontStats fontStats;
    private final INoteDetector noteDetector;
    private final ITradCitationExtractor citationExtractor;
    private final ICitationAnnotator<Citation, AnnotatedTradCitation> citationAnnotator;
    private final IHarvardCitationExtractor harvardExtractor;
    private final IBlocCitationExtractor blocExtractor;
    private final ICitationAnnotator<BlocCitation, AnnotatedBlocCitation> blocCitationAnnotator;
    private final IFootnoteExtractor footnoteExtractor;
    private final ITradCitationFootnoteAssociator footnoteAssociator;
    private final ICoordStats coordStats;

    private static final Logger logger = LoggerFactory.getLogger(Extractor.class);

    public Extractor(final IFontStats fontStats, final INoteDetector noteDetector,
            final ITradCitationExtractor citationExtractor, final ICitationAnnotator<Citation, AnnotatedTradCitation>citationAnnotator,
            final IHarvardCitationExtractor harvardExtractor, IBlocCitationExtractor blocExtractor,
            ICitationAnnotator<BlocCitation, AnnotatedBlocCitation> blocCitationAnnotator,
            final IFootnoteExtractor footnoteExtractor,
            ITradCitationFootnoteAssociator footnoteAssociator, ICoordStats coordStats) {
        this.fontStats = fontStats;
        this.noteDetector = noteDetector;
        this.citationExtractor = citationExtractor;
        this.citationAnnotator = citationAnnotator;
        this.harvardExtractor = harvardExtractor;
        this.blocExtractor = blocExtractor;
        this.blocCitationAnnotator = blocCitationAnnotator;
        this.footnoteExtractor = footnoteExtractor;
        this.footnoteAssociator = footnoteAssociator;
        this.coordStats = coordStats;
    }

    public AllTypeCitationsResult extractAll(PDDocument document) throws IOException {

        logger.info("Begin Extraction");
        try {
            LinkedHashMap<Integer, List<CitationWithNote>> tradCitations = extractTradCitations(document);
            LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations = extractHarvardCitations(document);

            // plug bloc citation to test
            ExtractBlocCitations(document);

            logger.info("Extraction ended with success");
            return new AllTypeCitationsResult(harvardCitations, tradCitations);
        } catch (IOException e) {
            logger.error("I/O Error during extraction ", e);
            throw new RuntimeException("Error during extraction", e);
        }

    }

    private LinkedHashMap<Integer, List<CitationWithNote>> extractTradCitations(PDDocument document)
            throws IOException {

        logger.debug("Starting trad citations extraction");

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<AnnotatedTradCitation>> foundCitations = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<Footnote>> footnotesPerPage = new LinkedHashMap<>();

        TroncatedCitation troncatedCitationFromLastPage = new TroncatedCitation(null, null);

        for (int page = 1; page <= pageCount; page++) {

            logger.debug("Analysing page {}", page);
            final int pageNum = page;

            if (troncatedCitationFromLastPage.content() != null) {
                logger.info("{}", troncatedCitationFromLastPage.content());
            }

            CustomTextStripper stripper = new CustomTextStripper();
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);

            // set extraction context
            List<TextPosition> positions = stripper.getTextPositions();
            ExtractionContext context = setExtractionContext(page, positions, document);

            // first : we get everything that's between quotation marks

            TradCitationExtractionResult result = citationExtractor.extractCitationsPerPage(context,
                    troncatedCitationFromLastPage);
            citationsCandidatesPerPage.put(page, result.citations());
            troncatedCitationFromLastPage = result.troncatedCitation();

            // second : we get all small number that can be a note calls
            notesCandidatesPerPage.put(page, noteDetector.getNoteCandidates(context));

            // third : we match to eleminate text between quotation marks that is note
            // citation
            foundCitations.put(page, citationAnnotator.getAnnotatedCitations(citationsCandidatesPerPage,
                    notesCandidatesPerPage, context));

            // forth : we get footnote content associate with note number
            footnotesPerPage.put(page, footnoteExtractor.getFootnotes(context, notesCandidatesPerPage));

            citationsCandidatesPerPage.get(page).forEach(citation -> {
                logger.debug("Citation trouvée page {} : \"{}...\"", pageNum,
                        citation.getText().substring(0, Math.min(40, citation.getText().length())));
            });

        }

        // finally : associate citation with footnote
        LinkedHashMap<Integer, List<CitationWithNote>> citationWithNote = footnoteAssociator
                .associateCitationWithFootnote(foundCitations, footnotesPerPage);

        return citationWithNote;
    }

    private LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> extractHarvardCitations(PDDocument document)
            throws IOException {

        logger.debug("Starting harvard citations extraction");

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations = new LinkedHashMap<>();

        TroncatedCitation troncatedCitationFromLastPage = new TroncatedCitation(null, null);

        for (int page = 1; page <= pageCount; page++) {

            logger.debug("Analysing page {}", page);
            final int pageNum = page;

            CustomTextStripper stripper = new CustomTextStripper();
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);

            // set extraction context
            List<TextPosition> positions = stripper.getTextPositions();
            ExtractionContext context = setExtractionContext(page, positions, document);

            HarvardCitationExtractionResult harvardCitationsResult = harvardExtractor.extractCitationsPerPage(context,
                    troncatedCitationFromLastPage);
            harvardCitations.put(page, harvardCitationsResult.harvardCitations());
            troncatedCitationFromLastPage = harvardCitationsResult.troncatedCitation();

            harvardCitations.get(page).forEach(citation -> {
                logger.debug("Citation trouvée page {} : \"{}...\"", pageNum,
                        citation.getBaseCitation().getText().substring(0,
                                Math.min(40, citation.getBaseCitation().getText().length())));
            });

        }

        return harvardCitations;
    }

    private void ExtractBlocCitations(PDDocument document) throws IOException {

        logger.debug("Starting bloc citations extraction");

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<BlocCitation>> blocCitations = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<AnnotatedBlocCitation>> foundCitations = new LinkedHashMap<>();

        List<Line> linesFromLastPage = new ArrayList<>();
        List<BlocCitation> citationsFromLastPage = new ArrayList<>();

        for (int page = 1; page <= pageCount; page++) {

            logger.debug("Analysing page {}", page);
            final int pageNum = page;

            CustomTextStripper stripper = new CustomTextStripper();
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);

            // set extraction context
            List<TextPosition> positions = stripper.getTextPositions();
            ExtractionContext context = setExtractionContext(page, positions, document);

            // 1 - Get the blocs
            BlocExtractionResult result = blocExtractor.extractCitationsPerPage(context,
                    linesFromLastPage, citationsFromLastPage);

            linesFromLastPage = result.lines();
            citationsFromLastPage = result.citation();
            blocCitations.put(page, result.citation());

            // 2 - Get the notes candidates
            notesCandidatesPerPage.put(page, noteDetector.getNoteCandidates(context));

            // 3 - Associate bloc citations and notes and eleminate false positive
            foundCitations.put(page,
                    blocCitationAnnotator.getAnnotatedCitations(blocCitations, notesCandidatesPerPage, context));

            // HarvardCitationExtractionResult harvardCitationsResult =
            // harvardExtractor.extractCitationsPerPage(context,
            // troncatedCitationFromLastPage);

            // // harvardCitations.put(page, harvardCitationsResult.harvardCitations());
            // troncatedCitationFromLastPage = harvardCitationsResult.troncatedCitation();

            // blocCitations.get(page).forEach(citation -> {
            // logger.debug("Citation trouvée page {} : \"{}...\"", pageNum,
            // citation.getBaseCitation().getText().substring(0,
            // Math.min(40, citation.getBaseCitation().getText().length())));
            // });

        }
        System.out.println(foundCitations);

    }

    private ExtractionContext setExtractionContext(int page, List<TextPosition> positions, PDDocument document)
            throws IOException {
        float avgFontSize = fontStats.getAverageFontSize(positions);
        float medianFontSize = fontStats.getMedianSize(positions);
        LineCoordStatsResult lineCoordStatsResult = coordStats.getLineCoordStats(document);
        return new ExtractionContext(positions, page, avgFontSize, medianFontSize, lineCoordStatsResult);
    }

}
