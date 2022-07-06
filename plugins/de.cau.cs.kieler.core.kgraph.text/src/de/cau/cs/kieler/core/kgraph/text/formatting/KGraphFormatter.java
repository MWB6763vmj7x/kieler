/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 * 
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.core.kgraph.text.formatting;

import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.util.Pair;

import de.cau.cs.kieler.core.kgraph.text.services.KGraphGrammarAccess;

/**
 * This class contains KGraph specific formatting descriptions.
 * 
 * @author chsch
 */
public class KGraphFormatter extends AbstractDeclarativeFormatter {

    @Override
    protected void configureFormatting(final FormattingConfig c) {
        KGraphGrammarAccess f = (KGraphGrammarAccess) getGrammarAccess();
        
        for (Pair<Keyword, Keyword> pair : f.findKeywordPairs("{", "}")) {
            c.setIndentation(pair.getFirst(), pair.getSecond());
            c.setLinewrap(1).after(pair.getFirst());
            c.setLinewrap(1).before(pair.getSecond());
            c.setLinewrap(1).after(pair.getSecond());
        }
        for (Keyword comma : f.findKeywords(",", ":")) {
            c.setNoLinewrap().before(comma);
            c.setNoSpace().before(comma);
            c.setLinewrap().after(comma);
        }
        for (Keyword word : f.findKeywords("mapProperties", "children", "styles", "bendPoints",
                "placementData", "detailedPlacementData", "sourcePoint", "targetPoint",
                "sourcePort", "targetPort", "topLeft", "bottomRight", "lineStyle", "lineWidth",
                "backgroundColor", "forgroundColor", "backgroundVisibility",
                "foregroundVisibility", "font", "fontSize", "fontColor", "bold", "italic",
                "horizontalAlignment", "verticalAlignment", "left", "right", "location", "xOffset",
                "width", "insets", "KInsets")) {
            c.setLinewrap().before(word);
        }
        for (Keyword word : f.findKeywords("points")) {
            c.setLinewrap().after(word);
        }

        c.setNoLinewrap().after(
                f.getKRenderingGrammarAccess().getKLayoutDataGrammarAccess().getKPointAccess()
                        .getXEFloatParserRuleCall_2_1_0());
//        c.setLinewrap().before(
//                f.getKRenderingGrammarAccess().getKLayoutDataGrammarAccess().getKEdgeLayoutAccess()
//                        .getSourcePointKeyword_2_0());
//        c.setLinewrap().before(
//                f.getKRenderingGrammarAccess().getKLayoutDataGrammarAccess().getKEdgeLayoutAccess()
//                        .getTargetPointKeyword_3_0());
        c.setLinewrap().after(
                f.getPersistentEntryAccess().getValueEStringParserRuleCall_1_1_0());
        
        c.setLinewrap(0, 1, 2).before(f.getSL_COMMENTRule());
        c.setLinewrap(0, 1, 2).before(f.getML_COMMENTRule());
        c.setLinewrap(0, 1, 1).after(f.getML_COMMENTRule());
    }
}
