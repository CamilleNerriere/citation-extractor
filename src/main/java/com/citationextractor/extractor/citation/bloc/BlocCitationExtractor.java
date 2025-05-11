/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor.citation.bloc;

import java.util.ArrayList;
import java.util.List;

import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.context.ExtractionContext;

/**
 *
 * @author camille
 */
public class BlocCitationExtractor implements IBlocCitationExtractor {
    @Override
    public void extractCitationsPerPage(ExtractionContext context,
            StringBuilder troncatedCitationFromLastPage) {
                // 
                List<BlocCitation> allCitations = new ArrayList<>();
                StringBuilder updatedTroncated = troncatedCitationFromLastPage;

                // C'est ici qu'on va récupérer les x de début et x de fin moyens

                


                
    }
}
