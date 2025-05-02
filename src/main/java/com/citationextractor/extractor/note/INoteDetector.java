/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.citationextractor.extractor.note;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.model.NoteCandidate;

/**
 *
 * @author camille
 */
public interface INoteDetector {
    List<NoteCandidate> getNoteCandidates(List<TextPosition> positions, int page, float avgFontSize);
}
