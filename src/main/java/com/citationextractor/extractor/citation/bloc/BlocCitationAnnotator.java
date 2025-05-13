package com.citationextractor.extractor.citation.bloc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedBlocCitation;
import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;

public class BlocCitationAnnotator implements IBlocCitationAnnotator {
    @Override
    public List<AnnotatedBlocCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<BlocCitation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context) {
                        
        List<AnnotatedBlocCitation> sortedCitations = new ArrayList<>();
        int page = context.getPage();

        List<BlocCitation> citations = citationsCandidatesPerPage.get(page);

        for (BlocCitation citation : citations) {

            float xCitationEnd = citation.getXEnd();
            float yCitation = citation.getYEnd();

            List<NoteCandidate> notesInPage = notesCandidatesPerPage.get(page);

            for (NoteCandidate note : notesInPage) {
                Float xNote = note.getX();
                Float yNote = note.getY();

                float dx = Math.abs(xNote - xCitationEnd);
                float dy = Math.abs(yNote - yCitation);

                if (dx < 35 && dy < 10) {
                    AnnotatedBlocCitation annotatedCitation = new AnnotatedBlocCitation(citation, note.getText());
                    sortedCitations.add(annotatedCitation);
                }
            }

        }

        return sortedCitations;

    }
}
