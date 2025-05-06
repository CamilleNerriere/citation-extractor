package com.citationextractor.exporter;

import com.citationextractor.model.context.ExporterContext;
import com.citationextractor.model.context.PdfCitationExporter;
import com.citationextractor.model.context.TxtCitationExporter;

public class ExporterFactory {
    
    public CitationExporter getExporter(ExporterContext context, String format) {
        return switch (format.toLowerCase()) {
            case "txt" -> new TxtCitationExporter(context);
            case "pdf" -> new PdfCitationExporter(context);
            default -> throw new IllegalArgumentException("FUnknown format : " + format);
        };
    }
}
