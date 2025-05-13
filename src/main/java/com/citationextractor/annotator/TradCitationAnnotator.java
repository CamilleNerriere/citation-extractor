package com.citationextractor.annotator;


import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.TradCitation;



public class TradCitationAnnotator extends GenericCitationAnnotator<TradCitation, AnnotatedTradCitation> implements ICitationAnnotator<TradCitation, AnnotatedTradCitation>{

     @Override
    protected float getXEnd(TradCitation citation) {
        return citation.getXEnd();
    }

    @Override
    protected float getYEnd(TradCitation citation) {
        return citation.getYEnd();
    }

    @Override
    protected AnnotatedTradCitation createAnnotated(TradCitation citation, String noteText) {
        return new AnnotatedTradCitation(citation, noteText);
    }
}
