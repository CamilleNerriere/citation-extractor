package com.citationextractor;

import java.io.File;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class App {
    public static void main(String[] args) {

        File file = new File("src/pdf/test.pdf");

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Extractor extractor = new Extractor();
            List<String> citations = extractor.extractAll(text);


            for (String citation : citations) {
                System.out.println(citation);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
