package com.citationextractor;

import java.io.File;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citationextractor.exporter.ExporterFactory;
import com.citationextractor.exporter.ICitationExporter;
import com.citationextractor.extractor.Extractor;
import com.citationextractor.extractor.citation.bloc.BlocCitationExtractor;
import com.citationextractor.extractor.citation.bloc.IBlocCitationExtractor;
import com.citationextractor.extractor.citation.harvard.HarvardCitationExtractor;
import com.citationextractor.extractor.citation.harvard.IHarvardCitationExtractor;
import com.citationextractor.extractor.citation.trad.ITradCitationAnnotator;
import com.citationextractor.extractor.citation.trad.ITradCitationExtractor;
import com.citationextractor.extractor.citation.trad.ITradCitationFootnoteAssociator;
import com.citationextractor.extractor.citation.trad.TradCitationAnnotator;
import com.citationextractor.extractor.citation.trad.TradCitationExtractor;
import com.citationextractor.extractor.citation.trad.TradCitationFootnoteAssociator;
import com.citationextractor.extractor.footnote.FootnoteExtractor;
import com.citationextractor.extractor.footnote.IFootnoteExtractor;
import com.citationextractor.extractor.note.INoteDetector;
import com.citationextractor.extractor.note.NoteDetector;
import com.citationextractor.model.context.ExporterContext;
import com.citationextractor.model.result.AllTypeCitationsResult;
import com.citationextractor.utils.FontStats;
import com.citationextractor.utils.IFontStats;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        if (args.length < 6) {
            System.out.println("Usage: java -jar citation-extractor.jar -i input.pdf -o output.pdf -f [pdf|txt]");
            return;
        }

        String inputPath = null, outputPath = null, format = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i" -> inputPath = args[++i];
                case "-o" -> outputPath = args[++i];
                case "-f" -> format = args[++i];
                default -> {
                    System.out.println("Unknown argument: " + args[i]);
                    return;
                }
            }
        }

        if (inputPath == null || outputPath == null || format == null) {
            System.out.println("Missing required arguments.");
            return;
        }

        File file = new File(inputPath);

        try (PDDocument document = Loader.loadPDF(file)) {

            logger.info("Starting app...");

            IFontStats fontStats = new FontStats();
            INoteDetector noteDetector = new NoteDetector();
            ITradCitationExtractor citationExtractor = new TradCitationExtractor();
            ITradCitationAnnotator citationAnnotator = new TradCitationAnnotator();
            IHarvardCitationExtractor harvardExtractor = new HarvardCitationExtractor();
            IBlocCitationExtractor blocExtractor = new BlocCitationExtractor();
            IFootnoteExtractor footnoteExtractor = new FootnoteExtractor();
            ITradCitationFootnoteAssociator footnoteAssociator = new TradCitationFootnoteAssociator();

            Extractor extractor = new Extractor(fontStats, noteDetector, citationExtractor, citationAnnotator,
                    harvardExtractor, blocExtractor, footnoteExtractor, footnoteAssociator);

            AllTypeCitationsResult citationsPerPage = extractor.extractAll(document);

            ExporterContext exporterContext = new ExporterContext(citationsPerPage.tradCitations(),
                    citationsPerPage.harvardCitations(), outputPath);

            ExporterFactory exporterFactory = new ExporterFactory();

            ICitationExporter citationExporter = exporterFactory.getExporter(exporterContext, format);

            logger.info("Exporting citations to PDF...");
            citationExporter.export();

            logger.info("Extraction and export completed successfully.");

        } catch (Exception e) {
            logger.error("Error during extractor execution", e);
            System.exit(1);
        }

    }

}
