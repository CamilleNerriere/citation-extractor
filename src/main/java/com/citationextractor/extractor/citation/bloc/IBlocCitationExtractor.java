/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.citationextractor.extractor.citation.bloc;

import com.citationextractor.model.citation.TroncatedCitation;
import com.citationextractor.model.context.ExtractionContext;

/**
 *
 * @author camille
 */
public interface IBlocCitationExtractor {
    void extractCitationsPerPage(ExtractionContext context,
                        TroncatedCitation troncatedCitationFromLastPage);
}
