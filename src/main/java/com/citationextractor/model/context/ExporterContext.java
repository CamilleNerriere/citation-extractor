package com.citationextractor.model.context;

import java.util.LinkedHashMap;
import java.util.List;

import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.TradCitationWithNote;

public class ExporterContext {

    private final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations;
    private final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations;
    private final String outPutPath;

    public ExporterContext(final LinkedHashMap<Integer, List<TradCitationWithNote>> tradCitations,
    final LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations,
    final String outPutPath) {
        this.tradCitations = tradCitations;
        this.harvardCitations = harvardCitations;
        this.outPutPath = outPutPath;
    }

    public LinkedHashMap<Integer, List<TradCitationWithNote>> getTradCitations(){
        return tradCitations;
    }

    public LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> getHarvardCitations(){
        return harvardCitations;
    }

    public String getOutputPath(){
        return outPutPath;
    }
    
}
