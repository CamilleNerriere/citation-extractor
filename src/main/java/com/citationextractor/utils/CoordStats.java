
package com.citationextractor.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.pdf.CustomTextStripper;

public class CoordStats implements ICoordStats {

    @Override
    public float getMedianXLineBegining(PDDocument document) throws IOException {

        CustomTextStripper stripper = new CustomTextStripper();
        stripper.clearPositions();
        stripper.getText(document);
        List<TextPosition> positions = stripper.getTextPositions();

        List<Float> xBeginnings = new ArrayList<>();

        if (!positions.isEmpty()) {
            xBeginnings.add(positions.get(0).getXDirAdj());
        }

        for (int i = 0; i < positions.size(); i++) {
            TextPosition lastPosition = i > 0 ? positions.get(i - 1) : null;
            TextPosition actualPosition = positions.get(i);

            float lastPosX = lastPosition != null ? lastPosition.getXDirAdj() : actualPosition.getXDirAdj();
            float actualPosX = actualPosition.getXDirAdj();

            float xDiff = actualPosX - lastPosX;

            if (xDiff < -20f) {
                xBeginnings.add(actualPosX);
            }

        }

        return MathUtils.getMedian(xBeginnings);
    }

    @Override
    public float getMedianXLineEnd(PDDocument document) throws IOException {
        float medianXLineEnd = 0f;

        return medianXLineEnd;
    }
}
