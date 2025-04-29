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

/**
 *
 * @author camille
 */
public class Extractor {

    public LinkedHashMap<Integer, List<String>> extractAll(PDDocument document) throws IOException {

        CustomTextStripper stripper = new CustomTextStripper();

        int pageCount = document.getNumberOfPages();

        LinkedHashMap<Integer, List<String>> citationsPerPage = new LinkedHashMap<>();

        for (int page = 1; page <= pageCount; page++) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);

            stripper.clearPositions();
            stripper.getText(document);
            List<TextPosition> positions = stripper.getTextPositions();
            Extractor extractor = new Extractor();
            citationsPerPage.put(page, extractor.extractCitationsPerPage(positions));
        }

        return citationsPerPage;
    }

    private List<String> extractCitationsPerPage(List<TextPosition> positions) {

        List<String> citations = new ArrayList<>();

        String[] openingQuotes = { "«", "\"", "“" };

        for (String opening : openingQuotes) {
            citations.addAll(extractCitations(positions, opening));
        }

        return citations;
    }

    private List<String> extractCitations(List<TextPosition> positions, String c1) {
        List<String> citations = new ArrayList<>();

        String c2 = switch (c1) {
            case "«" -> c2 = "»";
            case "\"" -> c2 = "\"";
            case "“" -> c2 = "”";
            default -> throw new IllegalArgumentException("Unauthorized Opening Quote" + c1);
        };

        for (int i = 0; i < positions.size(); i++) {
            int start;
            StringBuilder builder = new StringBuilder("");

            if (positions.get(i).getUnicode().equals(c1)) {
                System.out.println("Guillement ouvrant trouvé");
                start = i;
                for (int j = start; j < positions.size(); j++) {
                    if (!positions.get(j).getUnicode().equals(c2)) {
                        if (!positions.get(j).getUnicode().equals(c1)) {
                            builder.append(positions.get(j).getUnicode());
                        }
                    } else {
                        System.out.println("Guillement fermant trouvé");
                        System.out.println(builder);
                        citations.add(builder.toString().trim());
                        break;
                    }
                }
            }
        }
        return citations;
    }
}
