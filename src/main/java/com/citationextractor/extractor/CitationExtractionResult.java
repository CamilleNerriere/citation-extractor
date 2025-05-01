/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

package com.citationextractor.extractor;

import java.util.List;

/**
 *
 * @author camille
 */
public record CitationExtractionResult(List<Citation> citations, TroncatedCitation troncatedCitation) {

}
