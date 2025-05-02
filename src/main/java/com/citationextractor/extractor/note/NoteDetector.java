/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.citationextractor.extractor.note;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.NoteCandidate;

/**
 *
 * @author camille
 */
public class NoteDetector implements INoteDetector{

    @Override
    public List<NoteCandidate> getNoteCandidates(ExtractionContext context) {

        List<TextPosition> positions = context.getPositions();
        int page = context.getPage();
        float avgFontSize = context.getAverageFontSize();

        List<NoteCandidate> noteCandidates = new ArrayList<>();

        TextPosition lastNumberFound = null;
        StringBuilder completeNote = new StringBuilder("");
        float xStart = 0;
        float yStart = 0;

        for (TextPosition candidate : positions) {
            String c = candidate.getUnicode();
            if (c == null || c.length() == 0)
                continue;

            char ch = c.charAt(0);
            int type = Character.getType(ch);

            // Doit être un chiffre (chiffre décimal ou caractère comme ¹)
            if ((type == Character.DECIMAL_DIGIT_NUMBER || type == Character.OTHER_NUMBER)
                    && candidate.getFontSizeInPt() < avgFontSize * 0.75) {

                // a previous number has been found
                if (lastNumberFound != null) {

                    float deltaX = Math.abs(candidate.getXDirAdj() - lastNumberFound.getXDirAdj());
                    float deltaY = Math.abs(candidate.getYDirAdj() - lastNumberFound.getYDirAdj());

                    if (deltaX < 10 && deltaY < 10) { // it's close
                        completeNote.append(candidate.getUnicode());
                    } else { // it's not close -> start a new note
                        if (completeNote.length() > 0) {
                            noteCandidates.add(new NoteCandidate(completeNote.toString(), page, xStart, yStart));
                        }
                        completeNote.setLength(0);
                        completeNote.append(candidate.getUnicode());
                        xStart = candidate.getXDirAdj();
                        yStart = candidate.getYDirAdj();
                    }
                } else { // complete note is empty -> start a new note
                    completeNote.setLength(0);
                    completeNote.append(candidate.getUnicode());
                    xStart = candidate.getXDirAdj();
                    yStart = candidate.getYDirAdj();
                }

                lastNumberFound = candidate;
            }

        }

        if (completeNote.length() > 0) {
            noteCandidates.add(new NoteCandidate(completeNote.toString(), page, xStart, yStart));
        }

        return noteCandidates;
    }
}
