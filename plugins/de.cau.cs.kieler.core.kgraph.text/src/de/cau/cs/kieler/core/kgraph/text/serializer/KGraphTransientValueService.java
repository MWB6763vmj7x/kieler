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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService;
import org.eclipse.xtext.serializer.sequencer.TransientValueService;

import de.cau.cs.kieler.core.kgraph.KGraphPackage;

/**
 * A KGraph specific {@link ITransientValueService}. I implements also the old
 * {@link org.eclipse.xtext.parsetree.reconstr.ITransientValueService} since it is needed by the
 * current implementation of {@link org.eclipse.xtext.validation.IConcreteSyntaxValidator}.
 * 
 * @author chsch
 */
@SuppressWarnings("restriction")
public class KGraphTransientValueService extends TransientValueService implements
        ITransientValueService, org.eclipse.xtext.parsetree.reconstr.ITransientValueService {

    @Override
    public ListTransient isListTransient(final EObject semanticObject,
            final EStructuralFeature feature) {
        if (feature == KGraphPackage.eINSTANCE.getKNode_Parent()
                || feature == KGraphPackage.eINSTANCE.getKNode_IncomingEdges()
                || feature == KGraphPackage.eINSTANCE.getKEdge_Source()) {
            return ListTransient.YES;
        }
        return super.isListTransient(semanticObject, feature);
    }

    @Override
    public boolean isValueInListTransient(final EObject semanticObject, final int index,
            final EStructuralFeature feature) {
        if (feature == KGraphPackage.eINSTANCE.getKNode_Parent()
                || feature == KGraphPackage.eINSTANCE.getKNode_IncomingEdges()
                || feature == KGraphPackage.eINSTANCE.getKEdge_Source()) {
            return true;
        }
        return super.isValueInListTransient(semanticObject, index, feature);
    }

    @Override
    public ValueTransient isValueTransient(final EObject semanticObject,
            final EStructuralFeature feature) {
        if (feature == KGraphPackage.eINSTANCE.getKNode_Parent()
                || feature == KGraphPackage.eINSTANCE.getKNode_IncomingEdges()
                || feature == KGraphPackage.eINSTANCE.getKEdge_Source()) {
            return ValueTransient.YES;
        }
        return super.isValueTransient(semanticObject, feature);
    }

    // the above methods implement
    // org.eclipse.xtext.parsetree.reconstr.ITransientValueService
    // we need this since the current IConcreteSyntaxValidator relies on it
    /**
     * {@inheritDoc}
     */
    public boolean isCheckElementsIndividually(final EObject owner, final EStructuralFeature feature) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTransient(final EObject owner, final EStructuralFeature feature,
            final int index) {
        if (feature == KGraphPackage.eINSTANCE.getKNode_Parent()
                || feature == KGraphPackage.eINSTANCE.getKNode_IncomingEdges()
                || feature == KGraphPackage.eINSTANCE.getKEdge_Source()) {
            return true;
        }
        return false;
    }

}
