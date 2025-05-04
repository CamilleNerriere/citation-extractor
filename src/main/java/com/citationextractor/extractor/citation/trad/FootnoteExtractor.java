package com.citationextractor.extractor.citation.trad;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.footnote.Footnote;

public class FootnoteExtractor implements IFootnoteExtractor {

    @Override
    public List<Footnote> getFootnotes(
            ExtractionContext context,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage) {

        List<TextPosition> positions = context.getPositions();
        int page = context.getPage();

        List<NoteCandidate> pageNoteCandidates = notesCandidatesPerPage.get(page);

        List<NoteCandidate> footnoteCandidates = new ArrayList<>();

        if (pageNoteCandidates.isEmpty()) {
            return null;
        } else {
            for (NoteCandidate note : pageNoteCandidates) {
                String number = note.getText();

                Optional<NoteCandidate> candidate = pageNoteCandidates.stream()
                        .filter(n -> n.getText().equals(number))
                        .max(Comparator.comparing(NoteCandidate::getY));

                if (candidate.isPresent()) {
                    footnoteCandidates.add(candidate.get());
                } else {
                    return null;
                }
            }
        }

        List<Footnote> footnotes = new ArrayList<>();

        // on loop sur les positions : quand on arrive à la position de la note, on
        // commence à append jusqu'à ce qu'on trouve le chiffre suivant

        for (int i = 0; i < positions.size(); i++) {
            float xPosition = positions.get(i).getXDirAdj();
            float yPosition = positions.get(i).getYDirAdj();

            String c = positions.get(i).getUnicode();

            StringBuilder footnote = new StringBuilder();

            Optional<NoteCandidate> matchingCandidate = footnoteCandidates.stream()
                    .filter(note -> (note.getX() == xPosition && note.getY() == yPosition))
                    .findFirst();

            if (matchingCandidate.isPresent()) {
                NoteCandidate candidate = matchingCandidate.get();

                String noteText = candidate.getText();
                int noteNumberOfDigits = noteText.length();
                int candidateNoteNumber = Integer.parseInt(noteText);

                footnote.append(c);
                TextPosition startPos = positions.get(i);
                TextPosition endPos;

                for (int j = i + 1; j < positions.size(); j++) {

                    StringBuilder nextNoteNumber = new StringBuilder();
                    String cha = positions.get(j).getUnicode();

                    for (int k = j - (noteNumberOfDigits - 1); k <= j; k++) {
                        nextNoteNumber.append(positions.get(k).getUnicode());
                    }

                    if(isNumeric(nextNoteNumber.toString())){
                        int nextNumber = Integer.parseInt(nextNoteNumber.toString());

                        if(nextNumber - candidateNoteNumber == 1){
                            endPos = positions.get(j);
                            Footnote completeFootnote = new Footnote(footnote.toString(), page, noteText, startPos, endPos);
                            footnotes.add(completeFootnote);
                            break;
                        }
                    }

                    footnote.append(cha);
                }
            }
        }

        return footnotes;
    }

    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty())
            return false;

        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
