package com.citationextractor.footnoteAssociator;

import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.TradCitationWithNote;
import com.citationextractor.model.footnote.Footnote;

public class TradCitationFootnoteAssociator extends GenericFootnoteAssociator<TradCitationWithNote, AnnotatedTradCitation, Footnote> implements IFootnoteAssociator<TradCitationWithNote, AnnotatedTradCitation, Footnote> {
    @Override
    protected String getNoteNumberFromCitation(AnnotatedTradCitation citation){
        return citation.getNoteNumber();
    }

    @Override
    protected String getNoteNumberFromFootnote(Footnote footnote){
        return footnote.getNoteNumber();
    }

    @Override
    protected String getTextFromFootnote(Footnote footnote){
        return footnote.getText();
    }

    @Override
    protected TradCitationWithNote createCitationWithFootnote(AnnotatedTradCitation citation, String footnoteText){
        return new TradCitationWithNote(citation, footnoteText);
    }
}
