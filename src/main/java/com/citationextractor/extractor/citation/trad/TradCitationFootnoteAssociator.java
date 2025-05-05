package com.citationextractor.extractor.citation.trad;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.CitationWithNote;
import com.citationextractor.model.footnote.Footnote;

public class TradCitationFootnoteAssociator implements ITradCitationFootnoteAssociator {
    @Override
    public List<CitationWithNote> associateCitationWithFootnote(
            LinkedHashMap<Integer, List<AnnotatedTradCitation>> citations,
            LinkedHashMap<Integer, List<Footnote>> footnotes) {

        Set<Integer> pages = citations.keySet();
        List<CitationWithNote> citationsWithNotes = new ArrayList<>();

        for (int page : pages) {
            List<AnnotatedTradCitation> citationsPerPage = citations.get(page);
            List<Footnote> footnotePerPage = footnotes.get(page);

            List<CitationWithNote> citationsPerPageWithFootnotes = new ArrayList<>();

            for (AnnotatedTradCitation citation : citationsPerPage) {
                String noteNumber = citation.getNoteNumber();

                Footnote footnote = footnotePerPage.stream()
                        .filter(f -> f.getNoteNumber().equals(noteNumber)).findAny().orElse(null);

                if (footnote == null)
                    break;

                citationsPerPageWithFootnotes.add(new CitationWithNote(citation, footnote.getText()));
            }
            citationsWithNotes.addAll(citationsPerPageWithFootnotes);
        }


        System.out.println(citationsWithNotes);

        return citationsWithNotes;
    }
}
