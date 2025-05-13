package com.citationextractor.annotator;


import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.Citation;



public class TradCitationAnnotator extends GenericCitationAnnotator<Citation, AnnotatedTradCitation> implements ICitationAnnotator<Citation, AnnotatedTradCitation>{

     @Override
    protected float getXEnd(Citation citation) {
        return citation.getXEnd();
    }

    @Override
    protected float getYEnd(Citation citation) {
        return citation.getYEnd();
    }

    @Override
    protected AnnotatedTradCitation createAnnotated(Citation citation, String noteText) {
        return new AnnotatedTradCitation(citation, noteText);
    }
}
