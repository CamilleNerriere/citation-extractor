package com.citationextractor.model.context;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.citationextractor.exporter.CitationExporter;
import com.citationextractor.model.citation.AnnotatedHarvardCitation;
import com.citationextractor.model.citation.CitationWithNote;

public class TxtCitationExporter implements CitationExporter {

    private final ExporterContext context;

    public TxtCitationExporter(ExporterContext context) {
        this.context = context;

    }

    @Override
    public void export() {
        LinkedHashMap<Integer, List<CitationWithNote>> tradCitations = context.getTradCitations();
        LinkedHashMap<Integer, List<AnnotatedHarvardCitation>> harvardCitations = context.getHarvardCitations();
        String outPutPath = context.getOutputPath();

        StringBuilder txt = new StringBuilder();
        txt.append("-----------------Export Result---------------- \n");
        txt.append(System.lineSeparator());
        txt.append("--------------Classical Type Citation------------");
        txt.append(System.lineSeparator());

        Set<Integer> pages = tradCitations.keySet();

        for(int page : pages){
            txt.append(System.lineSeparator());
            txt.append("---------Page ").append(page).append("---------");
            txt.append(System.lineSeparator());

            for(CitationWithNote citation : tradCitations.get(page)){
                String cit = citation.getBaseAnnotatedCitation().getBaseCitation().getText();
                String noteNumber = citation.getBaseAnnotatedCitation().getNoteNumber();
                String footnote = citation.getFootnote();

                txt.append(System.lineSeparator());
                txt.append("Note ").append(noteNumber).append(" : ").append(cit).append("\n");
                txt.append("Footnote : ").append(footnote);
                txt.append(System.lineSeparator());
            }

            for(AnnotatedHarvardCitation citation : harvardCitations.get(page)){
                String cit = citation.getBaseCitation().getText();
                String note = citation.getNoteContent();

                txt.append(System.lineSeparator());
                txt.append(cit).append("\n");;
                txt.append("Note : ").append(note);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outPutPath));
            writer.write(txt.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("Unable to export citations in txt.");
        }
    }
}
