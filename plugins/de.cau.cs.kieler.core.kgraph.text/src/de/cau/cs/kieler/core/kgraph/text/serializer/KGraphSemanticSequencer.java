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
package de.cau.cs.kieler.core.kgraph.text.serializer;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.Inject;

import de.cau.cs.kieler.core.kgraph.text.services.KGraphGrammarAccess;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;

/**
 * Customization of the KGraph serialization.
 * The current customization are IMO bug fixes of the generated implementation.
 * 
 * The only difference of the given methods to their originals is the change
 * of the 'context' element that is delivered to the called delegate methods.
 * 
 * @author chsch
 */
public class KGraphSemanticSequencer extends AbstractKGraphSemanticSequencer {

    //CHECKSTYLEOFF Method

    @Inject
    private KGraphGrammarAccess grammarAccess;

    /**
     * Constraint:
     *     (
     *         xpos=EFloat? 
     *         ypos=EFloat? 
     *         width=EFloat? 
     *         height=EFloat? 
     *         insets=KInsets? 
     *         (persistentEntries+=PersistentEntry persistentEntries+=PersistentEntry*)?
     *     )
     *     
     */
    @Override
    protected void sequence_KShapeLayout(final EObject context, final KShapeLayout semanticObject) {
        genericSequencer.createSequence(grammarAccess.getKShapeLayoutRule(), semanticObject);
    }
    
    /**
     * Constraint:
     *     (
     *         sourcePoint=KPoint 
     *         targetPoint=KPoint 
     *         (bendPoints+=KPoint bendPoints+=KPoint*)? 
     *         (persistentEntries+=PersistentEntry persistentEntries+=PersistentEntry*)?
     *     )
     */
    @Override
    protected void sequence_KEdgeLayout(final EObject context, final KEdgeLayout semanticObject) {
        genericSequencer.createSequence(grammarAccess.getKEdgeLayoutRule(), semanticObject);
    }
    
    //CHECKSTYLEON Method

}
