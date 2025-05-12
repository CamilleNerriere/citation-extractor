/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.citationextractor.extractor.citation.bloc;

import java.util.List;

import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.result.BlocExtractionResult;
import com.citationextractor.model.text.Line;

/**
 *
 * @author camille
 */
public interface IBlocCitationExtractor {
    BlocExtractionResult extractCitationsPerPage(ExtractionContext context,
                            List<Line> linesFromLastPage, List<BlocCitation> citationsFromLastPage) ;
}
