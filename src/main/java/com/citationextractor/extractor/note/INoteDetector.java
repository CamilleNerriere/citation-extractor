/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.citationextractor.extractor.note;

import java.util.List;

import com.citationextractor.extractor.context.ExtractionContext;
import com.citationextractor.model.NoteCandidate;

/**
 *
 * @author camille
 */
public interface INoteDetector {
    List<NoteCandidate> getNoteCandidates(ExtractionContext context);
}
