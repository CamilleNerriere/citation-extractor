package com.citationextractor.model.context;

import com.citationextractor.exporter.CitationExporter;

public class PdfCitationExporter implements CitationExporter{

    private final ExporterContext context;

    public PdfCitationExporter(ExporterContext context) {
        this.context = context;

    }
    public void export(){

    }
}
