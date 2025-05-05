package com.citationextractor.extractor.footnote;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (pageNoteCandidates == null || pageNoteCandidates.isEmpty()) {
            return new ArrayList<>();
        }

        List<NoteCandidate> footnoteCandidates = getFootnoteCandidates(pageNoteCandidates);


        List<Footnote> footnotes = new ArrayList<>();
        Set<Integer> alreadyTreatedCandidateNumbers = new HashSet<>();

        for (int i = 0; i < positions.size(); i++) {
            TextPosition pos = positions.get(i);
            float xPosition = pos.getXDirAdj();
            float yPosition = pos.getYDirAdj();
            String c = pos.getUnicode();

            Optional<NoteCandidate> matchingCandidate = footnoteCandidates.stream()
                    .filter(note -> {
                        float deltaX = Math.abs(note.getX() - xPosition);
                        float deltaY = Math.abs(note.getY() - yPosition);
                        return deltaX < 10.0f && deltaY < 10.0f;
                    })
                    .findFirst();

            /* We find a match, start the treatment */
            if (matchingCandidate.isPresent()) {

                NoteCandidate candidate = matchingCandidate.get();

                String noteNumberAsString = candidate.getText();
                if (!isNumeric(noteNumberAsString)) {
                    continue;
                }

                int candidateNoteNumber = Integer.parseInt(noteNumberAsString);

                // Avoid multiple treatment
                if (alreadyTreatedCandidateNumbers.contains(candidateNoteNumber)) {
                    continue;
                }
                alreadyTreatedCandidateNumbers.add(candidateNoteNumber);

                NoteCandidate nextNoteCandidate = getNextNoteCandidate(footnoteCandidates, candidateNoteNumber);

                /** Set variables to stock footnote infos */
                StringBuilder footnote = new StringBuilder();
                footnote.append(c);

                TextPosition startPos = positions.get(i);
                TextPosition endPos;
                boolean noteEnded = false;

                // Start finding next characters

                for (int j = i + 1; j < positions.size(); j++) {

                    TextPosition newPos = positions.get(j);
                    String cha = newPos.getUnicode();

                    // Loop ends if we're approximatly at the next note position

                    if (nextNoteCandidate != null) {
                        float XNextNote = nextNoteCandidate.getX();
                        float YNextNote = nextNoteCandidate.getY();

                        float deltaX = Math.abs(newPos.getXDirAdj() - XNextNote);
                        float deltaY = Math.abs(newPos.getYDirAdj() - YNextNote);

                        if (deltaX < 10.0f && deltaY < 10.0f) {
                            endPos = positions.get(j - 1);
                            Footnote completeFootnote = new Footnote(footnote.toString(), page, noteNumberAsString,
                                    startPos, endPos);
                            footnotes.add(completeFootnote);
                            footnote.setLength(0);
                            noteEnded = true;
                            break;
                        }
                    }

                    footnote.append(cha);
                }

                // in case it's the last note on page
                if (!noteEnded && footnote.length() > 0) {
                    endPos = positions.get(positions.size() - 1);
                    Footnote completeFootnote = new Footnote(footnote.toString(), page, noteNumberAsString, startPos,
                            endPos);
                    footnotes.add(completeFootnote);
                    footnote.setLength(0);
                }
            }

        }

        return footnotes;
    }

    private List<NoteCandidate> getFootnoteCandidates(List<NoteCandidate> pageNoteCandidates) {
        return pageNoteCandidates.stream()
                .collect(Collectors.groupingBy(NoteCandidate::getText))
                .values().stream()
                .map(group -> group.stream()
                        .max(Comparator.comparing(NoteCandidate::getY))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private NoteCandidate getNextNoteCandidate(List<NoteCandidate> footnoteCandidates, int candidateNoteNumber) {
        List<Integer> noteNumbers = new ArrayList<>();

        for (NoteCandidate note : footnoteCandidates) {
            int noteNumber = Integer.parseInt(note.getText());
            noteNumbers.add(noteNumber);
        }

        int nextNoteNumber = -1;

        for (int num : noteNumbers) {
            if (num - candidateNoteNumber == 1) {
                nextNoteNumber = num;
            }
        }

        final int finalNextNoteNumber = nextNoteNumber;

        Optional<NoteCandidate> nextNoteCandidate = footnoteCandidates.stream()
                .filter(note -> note.getText().equals(Integer.toString(finalNextNoteNumber)))
                .findAny();

        if (nextNoteCandidate.isPresent()) {
            return nextNoteCandidate.get();
        }

        return null;
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
