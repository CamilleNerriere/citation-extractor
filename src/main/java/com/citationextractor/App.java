package com.citationextractor;

import java.io.File;
import java.util.ArrayList;
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

            char[] textAsArray = text.toCharArray();

            char c1 = '«';
            char c2 = '"';
            char c3 = '“';

            List<String> citations = extractCitations(textAsArray, c1);
            citations.addAll(extractCitations(textAsArray, c2));
            citations.addAll(extractCitations(textAsArray, c3));

            for (String citation : citations) {
                System.out.println(citation);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    static List<String> extractCitations(char[] textAsArray, char c1){
        List<String> citations = new ArrayList<>();

        char c2 = 0;

        switch (c1) {
            case '«':
                c2= '»';
                break;
            case '"':
                c2 = '"';
                break;
            case '“':
                c2 = '”';
                break;
            default:
            throw new IllegalArgumentException("Guillemet ouvrant non reconnu : " + c1);
        }

        for (int i = 0; i < textAsArray.length; i++) {
            int start;
            StringBuilder builder = new StringBuilder("");
            

            if (textAsArray[i] == c1) {
                start = i;
                for (int j = start; j < textAsArray.length; j++) {
                    if (textAsArray[j] != c2) {

                        if (textAsArray[j] != c1) {
                            builder.append(textAsArray[j]);
                        }
                    } else {
                        citations.add(builder.toString().trim());
                        builder.delete(0, builder.length());
                        break;
                    }
                }
            }
        }
        return citations;
    }

}
