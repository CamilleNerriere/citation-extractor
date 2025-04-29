/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author camille
 */
public class Extractor {

    public List<String> extractAll(String text) {
        char[] textAsArray = text.toCharArray();


        List<String> citations = new ArrayList<>();

        char[] openingQuotes = {'«', '"', '“'};

        for (char opening : openingQuotes) {
            citations.addAll(extractCitations(textAsArray, opening));
        }

        return citations;
    }

    private List<String> extractCitations(char[] textAsArray, char c1) {
        List<String> citations = new ArrayList<>();

        char c2 = 
        switch (c1) {
            case '«' -> c2 = '»';
            case '"' -> c2 = '"';
            case '“' -> c2 = '”';
            default -> throw new IllegalArgumentException("Unauthorized Opening Quote" + c1);
        } ;

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
                        break;
                    }
                }
            }
        }
        return citations;
    }
}
