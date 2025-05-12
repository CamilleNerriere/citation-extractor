package com.citationextractor.extractor.citation.trad;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedTradCitation;
import com.citationextractor.model.citation.Citation;
import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;

public class TradCitationAnnotator implements ITradCitationAnnotator {

    @Override
    public List<AnnotatedTradCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context) {

        List<AnnotatedTradCitation> sortedCitations = new ArrayList<>();
        int page = context.getPage();

        List<Citation> citations = citationsCandidatesPerPage.get(page);

        for (Citation citation : citations) {

            float xCitationEnd = citation.getXEnd();
            float yCitation = citation.getYEnd();

            List<NoteCandidate> notesInPage = notesCandidatesPerPage.get(page);

            for (NoteCandidate note : notesInPage) {
                Float xNote = note.getX();
                Float yNote = note.getY();

                float dx = Math.abs(xNote - xCitationEnd);
                float dy = Math.abs(yNote - yCitation);

                if (dx < 25 && dy < 10) {
                    AnnotatedTradCitation annotatedCitation = new AnnotatedTradCitation(citation, note.getText());
                    sortedCitations.add(annotatedCitation);
                }
            }

        }
        return sortedCitations;
    }
}
