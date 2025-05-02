/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

package com.citationextractor.model;

/**
 *
 * @author camille
 */
public record OneCitationResult(Citation citation, TroncatedCitation trunc) {
    public boolean citationIsEmpty() {
        return citation.getText() == null || citation.getText().isEmpty();
    }
}
