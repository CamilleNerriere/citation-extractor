package com.citationextractor;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.citationextractor.extractor.Extractor;
import com.citationextractor.extractor.citation.HarvardCitationExtractor;
import com.citationextractor.extractor.citation.IHarvardCitationExtractor;
import com.citationextractor.extractor.citation.ITradCitationAnnotator;
import com.citationextractor.extractor.citation.ITradCitationExtractor;
import com.citationextractor.extractor.citation.TradCitationAnnotator;
import com.citationextractor.extractor.citation.TradCitationExtractor;
import com.citationextractor.extractor.note.INoteDetector;
import com.citationextractor.extractor.note.NoteDetector;
import com.citationextractor.model.Citation;
import com.citationextractor.utils.FontStats;
import com.citationextractor.utils.IFontStats;

public class App {
    public static void main(String[] args) {

        File file = new File("src/pdf/test.pdf");

        try (PDDocument document = Loader.loadPDF(file)) {
            
            IFontStats fontStats = new FontStats();
            INoteDetector noteDetector = new NoteDetector();
            ITradCitationExtractor citationExtractor = new TradCitationExtractor();
            ITradCitationAnnotator citationAnnotator = new TradCitationAnnotator();
            IHarvardCitationExtractor harvardExtractor = new HarvardCitationExtractor();
            Extractor extractor = new Extractor(fontStats, noteDetector, citationExtractor, citationAnnotator, harvardExtractor);

            LinkedHashMap<Integer, List<Citation>> citationsPerPage = extractor.extractAll(document);

            // for (int i : citationsPerPage.keySet()) {
            //     System.out.println("Page " + i + " : " + citationsPerPage.get(i));
            // }
            
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }

}
