package com.citationextractor;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.citationextractor.extractor.Citation;
import com.citationextractor.extractor.Extractor;

public class App {
    public static void main(String[] args) {

        File file = new File("src/pdf/test.pdf");

        try (PDDocument document = Loader.loadPDF(file)) {
            
            Extractor extractor = new Extractor();

            LinkedHashMap<Integer, List<Citation>> citationsPerPage = extractor.extractAll(document);

            // for (int i : citationsPerPage.keySet()) {
            //     System.out.println("Page " + i + " : " + citationsPerPage.get(i));
            // }
            
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
