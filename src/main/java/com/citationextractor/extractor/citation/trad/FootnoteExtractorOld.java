package com.citationextractor.extractor.citation.trad;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.model.citation.NoteCandidate;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.footnote.Footnote;

public class FootnoteExtractorOld implements IFootnoteExtractor {

    @Override
    public List<Footnote> getFootnotes(
            ExtractionContext context,
            LinkedHashMap<Integer, List<NoteCandidate>> notesCandidatesPerPage) {

        List<TextPosition> positions = context.getPositions();
        positions.sort(Comparator
                .comparing(TextPosition::getYDirAdj).reversed()
                .thenComparing(TextPosition::getXDirAdj));

        int page = context.getPage();

        List<NoteCandidate> pageNoteCandidates = notesCandidatesPerPage.get(page);

        // List<NoteCandidate> footnoteCandidates = new ArrayList<>();

        // if (pageNoteCandidates.isEmpty()) {
        // return null;
        // } else {
        // for (NoteCandidate note : pageNoteCandidates) {
        // String number = note.getText();

        // Optional<NoteCandidate> candidate = pageNoteCandidates.stream()
        // .filter(n -> n.getText().equals(number))
        // .max(Comparator.comparing(NoteCandidate::getY));

        // if (candidate.isPresent()) {
        // footnoteCandidates.add(candidate.get());
        // } else {
        // return null;
        // }
        // }
        // }

        if (pageNoteCandidates == null || pageNoteCandidates.isEmpty()) {
            return null;
        }

        // deduplicate notes candidates and keep the right one : we want the ones on the
        // bottom of the page
        List<NoteCandidate> uniqueCandidates = deduplicateCandidates(pageNoteCandidates);

        Map<String, NoteCandidate> bestCandidateByNumber = new HashMap<>();

        for (NoteCandidate note : uniqueCandidates) {
            String number = note.getText();
            NoteCandidate existing = bestCandidateByNumber.get(number);

            if (existing == null || note.getY() > existing.getY()) {
                bestCandidateByNumber.put(number, note);
            }
        }

        // sort footnote candidates
        List<NoteCandidate> footnoteCandidates = new ArrayList<>(bestCandidateByNumber.values());

        footnoteCandidates.sort(Comparator.comparing(note -> {
            try {
                return Integer.valueOf(note.getText());
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE; // if non numeric value
            }
        }));

        // log to check if we have candidates
        System.out.println("Page " + page + " - Final sorted footnote candidates: " + footnoteCandidates.size());

        for (NoteCandidate candidate : footnoteCandidates) {
            System.out.println(
                    "  Candidate: " + candidate.getText() + " at (" + candidate.getX() + "," + candidate.getY() + ")");
        }

        // average X position for footnote's number
        float averageX = (float) footnoteCandidates.stream()
                .mapToDouble(NoteCandidate::getX)
                .average()
                .orElse(0);
        System.out.println("Average X position for notes: " + averageX);

        // create a map with rounded coordinates as key, and the candidate as value
        Map<String, NoteCandidate> candidatesByPosition = buildPositionMap(footnoteCandidates);

        List<Footnote> footnotes = new ArrayList<>();
        Set<Integer> alreadyExtractedNotes = new HashSet<>();

        for (int i = 0; i < positions.size(); i++) {
            TextPosition pos = positions.get(i);
            float xPosition = pos.getXDirAdj();
            float yPosition = pos.getYDirAdj();
            String c = pos.getUnicode();

            // claude suggestion : generate a key to quicckly search
            String positionKey = getPositionKey(xPosition, yPosition);
            NoteCandidate candidate = candidatesByPosition.get(positionKey);

            if (candidate != null) {
                // System.out.println("Found matching candidate at position (" + xPosition + "," + yPosition + "): "
                //         + candidate.getText());

                String noteText = candidate.getText();
                if (!isNumeric(noteText)) {
                    continue;
                }

                int candidateNoteNumber = Integer.parseInt(noteText);

                // Éviter le double traitement
                if (alreadyExtractedNotes.contains(candidateNoteNumber)) {
                    System.out.println("  Note " + candidateNoteNumber + " already processed, skipping");
                    continue;
                }

                alreadyExtractedNotes.add(candidateNoteNumber);
                // System.out.println("  Processing note " + candidateNoteNumber);

                StringBuilder footnote = new StringBuilder();

                // Optional<NoteCandidate> matchingCandidate = footnoteCandidates.stream()
                // .filter(note -> {
                // float deltaX = Math.abs(note.getX() - xPosition);
                // float deltaY = Math.abs(note.getY() - yPosition);
                // return deltaX < 10.0f && deltaY < 10.0f;
                // })
                // .findFirst();

                // if (matchingCandidate.isPresent()) {

                // NoteCandidate candidate = matchingCandidate.get();

                // System.out.println("Processing matching candidate: " + matchingCandidate);

                // String noteText = candidate.getText();
                // if (!isNumeric(noteText)) {
                // continue;
                // }

                // int candidateNoteNumber = Integer.parseInt(noteText);

                // // Avoid double treatment
                // if (alreadyExtractedNotes.contains(candidateNoteNumber)) {
                // continue;
                // }
                // alreadyExtractedNotes.add(candidateNoteNumber);

                footnote.append(c);
                TextPosition startPos = positions.get(i);
                TextPosition endPos;

                boolean noteEnded = false;

                for (int j = i + 1; j < positions.size(); j++) {

                    String cha = positions.get(j).getUnicode();

                    int expectedNumberOfDigits = noteText.length();

                    // to detect when a number increases in length (such as 9 to 10)
                    if (isAllNines(candidateNoteNumber)) {
                        expectedNumberOfDigits += 1;
                    }

                    String nextNoteNumber = getNextNoteNumber(positions, j, expectedNumberOfDigits);

                    // ici on vient regarder si la position actuelle (x) est à peu près similaire

                    float deltaX = Math.abs(positions.get(j).getXDirAdj() - averageX);
                    boolean isNearLeftColumn = deltaX < 10.0f;

                    if (isNumeric(nextNoteNumber)) {

                        try {
                            int nextNumber = Integer.parseInt(nextNoteNumber);

                            if (nextNumber - candidateNoteNumber == 1 && isNearLeftColumn) {
                                endPos = positions.get(j - 1);
                                Footnote completeFootnote = new Footnote(footnote.toString(), page, noteText, startPos,
                                        endPos);
                                footnotes.add(completeFootnote);
                                System.out.println("  Added complete footnote: " + noteText + " -> "
                                        + footnote.toString().substring(0, Math.min(20, footnote.length())) + "...");
                                footnote.setLength(0);
                                noteEnded = true;
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Unable to parse nextNumber");
                        }

                        footnote.append(cha);
                    }

                }
                // handle the last note on page
                if (!noteEnded && footnote.length() > 0) {
                    endPos = positions.get(positions.size() - 1);
                    Footnote completeFootnote = new Footnote(footnote.toString(), page, noteText, startPos, endPos);
                    footnotes.add(completeFootnote);
                    System.out.println("  Added last footnote on page: " + noteText);
                    footnote.setLength(0);
                }
            }
            // System.out.println("Page " + page + " - Extracted " + footnotes.size() + " footnotes");
        }
        return footnotes;
    }

    private String getPositionKey(float x, float y) {
        // round to 5 units to tolerate small differences
        int roundedX = Math.round(x / 5) * 5;
        int roundedY = Math.round(y / 5) * 5;
        return roundedX + "_" + roundedY;
    }

    private Map<String, NoteCandidate> buildPositionMap(List<NoteCandidate> footnoteCandidates) {
        Map<String, NoteCandidate> map = new HashMap<>();
        for (NoteCandidate candidate : footnoteCandidates) {
            String key = getPositionKey(candidate.getX(), candidate.getY());
            map.put(key, candidate);
        }
        return map;
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

    private boolean isAllNines(int number) {
        while (number > 0) {
            if (number % 10 != 9) {
                return false;
            }
            number /= 10;
        }
        return true;
    }

    private String getNextNoteNumber(List<TextPosition> positions, int start, int expectedNumberOfDigits) {
        int maxLookahead = 30; // Limit to avoid to go to far
        StringBuilder number = new StringBuilder();

        boolean foundDigit = false;

        for (int k = start; k < positions.size() && k <= start + maxLookahead; k++) {
            String unicode = positions.get(k).getUnicode();

            if (foundDigit && !Character.isDigit(unicode.charAt(0))) {

                if (number.length() == expectedNumberOfDigits)
                    return number.toString();

                return null;
            }

            // if digit we add it to our number
            if (Character.isDigit(unicode.charAt(0))) {
                number.append(unicode);
                foundDigit = true;
            }
            // Si on avait commencé à lire un chiffre et que ça s’arrête, on casse
            else if (number.length() > 0) {
                break;
            }
        }

        if (number.length() == expectedNumberOfDigits) {
            return number.toString();
        }

        return null;
    }

    private List<NoteCandidate> deduplicateCandidates(List<NoteCandidate> candidates) {
        return candidates.stream()
                .collect(Collectors.toMap(
                        candidate -> candidate.getText() + "_" + candidate.getX() + "_" + candidate.getY(),
                        candidate -> candidate,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .collect(Collectors.toList());
    }
}
