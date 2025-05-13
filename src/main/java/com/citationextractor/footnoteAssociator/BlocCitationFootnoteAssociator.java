package com.citationextractor.footnoteAssociator;

import com.citationextractor.model.citation.AnnotatedBlocCitation;
import com.citationextractor.model.citation.BlocCitationWithNote;
import com.citationextractor.model.footnote.Footnote;

public class BlocCitationFootnoteAssociator extends GenericFootnoteAssociator<BlocCitationWithNote, AnnotatedBlocCitation, Footnote> implements IFootnoteAssociator<BlocCitationWithNote, AnnotatedBlocCitation, Footnote> {
    @Override
    protected String getNoteNumberFromCitation(AnnotatedBlocCitation citation){
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
    protected BlocCitationWithNote createCitationWithFootnote(AnnotatedBlocCitation citation, String footnoteText){
        return new BlocCitationWithNote(citation, footnoteText);
    }
}
