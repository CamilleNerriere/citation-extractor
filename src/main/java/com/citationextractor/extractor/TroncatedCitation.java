/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

package com.citationextractor.extractor;

/**
 *
 * @author camille
 */
public record TroncatedCitation(String content, String openingQuote) {
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}
