/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.citationextractor.utils;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author camille
 */
public interface IFontStats {
    float getAverageFontSize(List<TextPosition> positions);
    float getMedianSize(List<TextPosition> positions);
}
