package com.citationextractor.extractor.citation.bloc;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import com.citationextractor.model.citation.BlocCitation;
import com.citationextractor.model.context.ExtractionContext;
import com.citationextractor.model.result.BlocCitationExtractionResult;

public class BlocCitationExtractorOld implements IBlocCitationExtractor {
    @Override
    public void extractCitationsPerPage(ExtractionContext context,
            StringBuilder troncatedCitationFromLastPage) {
       
                List<BlocCitation> allCitations = new ArrayList<>();
                StringBuilder updatedTroncated = troncatedCitationFromLastPage;

                int page = context.getPage();
                List<TextPosition> positions = context.getPositions();
                float medianXLineBegining = context.getLineCoordStatsResult().medianXLineBegining();
                float medianXLineEnd = context.getLineCoordStatsResult().medianXLineEnd();

                List<BlocCitation> blocCitations = new ArrayList<>();
                
                for (int i = 0; i < positions.size(); i++) {
                    
                    BlocCitationExtractionResult result = null;
                    TextPosition firstPos = positions.get(i);
                    String firstChar = firstPos.getUnicode();

                    StringBuilder citation = new StringBuilder();

                    float xDiff = firstPos.getXDirAdj() - medianXLineBegining;

                    // il faut vérifier si c'est le premier de sa ligne 

                    float lastX = i > 0 ? positions.get(i -1).getXDirAdj() : firstPos.getXDirAdj();

                    if(xDiff > 20f & firstPos.getXDirAdj() - lastX > 20f){

                        // System.out.println("INDENTATION A GAUCHE");
                        
                        citation.append(firstChar);
                        float xStart = firstPos.getXDirAdj();

                        boolean isNewLine = false;

                        int j = i + 1;
                        
                        for (; j < positions.size(); j++) {

                            TextPosition secondPos = positions.get(j);
                            String secondChar = secondPos.getUnicode();

                            float xEnd = secondPos.getXDirAdj();

                            float newXDiff = Math.abs(xEnd - xStart);

                            // on vient quand on va passer à la ligne pour la première ligne

                            // case 1 : we're one first line and have to check if we're really in a bloc citation

                            if(!isNewLine && !(newXDiff >= 1f)){
                                isNewLine = true;

                                float lastCharX = positions.get(j-1).getXDirAdj();

                                float xEndDiff = medianXLineBegining - lastCharX;

                                if(!(xEndDiff > 10f)){
                                    System.out.println("ON COUPE CE N'EST PAS UNE CITATION");
                                    break;
                                }
                            }

                            float Xref = firstPos.getXDirAdj();
                            float actualX = secondPos.getXDirAdj();

                            float XDiff3 = actualX - Xref;

                            // case 2 : we encountered a new line with regular x -> citation ends

                            if(XDiff3 < -3f){
                                BlocCitation blocCitation = new BlocCitation(citation.toString(), page, firstPos, secondPos);
                                allCitations.add(blocCitation);
                                updatedTroncated.setLength(0);
                                break;
                            }

                            // case 3 : citation not ended : we keep adding
                            citation.append(secondChar);
                            
                            // truncated citation to handle next page
                            if(citation.length() > 0){
                                updatedTroncated = citation;
                            }
                            
                        }
                        i = j - 1;
                    }


                }

                System.out.println(allCitations);
                
    }
}
