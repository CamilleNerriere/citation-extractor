package com.citationextractor.extractor.citation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.AnnotatedCitation;
import com.citationextractor.model.Citation;
import com.citationextractor.model.NoteCandidate;


public class CitationAnnotator implements ICitationAnnotator{

    @Override
    public List<AnnotatedCitation> getAnnotatedCitations(
            LinkedHashMap<Integer, List<Citation>> citationsCandidatesPerPage,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage, ExtractionContext context) {

        List<AnnotatedCitation> sortedCitations = new ArrayList<>();
        int page = context.getPage();

        List<Citation> citations = citationsCandidatesPerPage.get(page);

        for (Citation citation : citations) {

            float xCitationEnd = citation.getXEnd();
            float yCitation = citation.getYEnd();

            List<NoteCandidate> notesInPage = notesCandidatesPerPage.get(page);

            for (NoteCandidate note : notesInPage) {
                Float xNote = note.getX();
                Float yNote = note.getY();

                float dx = xNote - xCitationEnd;
                float dy = Math.abs(yNote - yCitation);

                if (dx >= -1 && dx < 20 && dy < 25) {
                    AnnotatedCitation annotatedCitation = new AnnotatedCitation(citation, note.getText());
                    sortedCitations.add(annotatedCitation);
                }
            }

        }
        return sortedCitations;
    }
}

// TODO : adapter les conditions en fonction du type de note parce que spatialisation différente