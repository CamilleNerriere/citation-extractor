package com.citationextractor.exporter;

import com.citationextractor.model.context.ExporterContext;

public class ExporterFactory {
    
    public ICitationExporter getExporter(ExporterContext context, String format) {
        return switch (format.toLowerCase()) {
            case "txt" -> new TxtCitationExporter(context);
            case "pdf" -> new PdfCitationExporter(context);
            default -> throw new IllegalArgumentException("Unknown format : " + format);
        };
    }
}
